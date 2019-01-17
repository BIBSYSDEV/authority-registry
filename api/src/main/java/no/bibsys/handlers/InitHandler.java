package no.bibsys.handlers;

import com.amazonaws.services.apigateway.AmazonApiGateway;
import com.amazonaws.services.apigateway.AmazonApiGatewayClientBuilder;
import com.amazonaws.services.apigateway.model.NotFoundException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.route53.model.Change;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.annotations.VisibleForTesting;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import no.bibsys.EnvironmentVariables;
import no.bibsys.aws.apigateway.ApiGatewayApiInfo;
import no.bibsys.aws.apigateway.ServerInfo;
import no.bibsys.aws.cloudformation.Stage;
import no.bibsys.aws.cloudformation.helpers.ResourceType;
import no.bibsys.aws.cloudformation.helpers.StackResources;
import no.bibsys.aws.lambda.events.DeployEvent;
import no.bibsys.aws.lambda.responses.SimpleResponse;
import no.bibsys.aws.swaggerhub.SwaggerDriver;
import no.bibsys.aws.swaggerhub.SwaggerHubInfo;
import no.bibsys.aws.tools.Environment;
import no.bibsys.service.AuthenticationService;
import no.bibsys.staticurl.UrlUpdater;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitHandler extends ResourceHandler {

    public static final String BASE_PATH_FIELD = "basePath";
    public static final String URL_FIELD = "url";
    public static final String DEFAULT_FIELD = "default";
    public static final String VARIABLES_FIELD = "variables";
    public static final String SERVERS_FIELD = "servers";
    private final static Logger logger = LoggerFactory.getLogger(InitHandler.class);
    private final transient AuthenticationService authenticationService;
    private final transient String certificateArn;

    private final transient String apiId;
    private final transient String apiVersion;
    private final transient String swaggerOrganization;
    private final transient String stackName;
    private final transient Stage stage;

    private final transient ObjectMapper jsonParser = Json.mapper(); //Swagger specific ObjectMapper

    public InitHandler() {
        this(new Environment());

    }

    public InitHandler(Environment environment) {
        super(environment);
        final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        this.certificateArn = environment.readEnv(EnvironmentVariables.CERTIFICATE_ARN_ENV);
        this.apiId = environment.readEnv(EnvironmentVariables.SWAGGER_API_ID);
        this.apiVersion = environment.readEnv(EnvironmentVariables.SWAGGER_API_VERSION);
        this.swaggerOrganization = environment.readEnv(EnvironmentVariables.SWAGGER_API_OWNER);
        this.stackName = environment.readEnv(EnvironmentVariables.STACK_NAME);
        this.stage = Stage.fromString(environment.readEnv(EnvironmentVariables.STAGE_NAME));
        authenticationService = new AuthenticationService(client, environment);
    }


    @Override
    protected SimpleResponse processInput(DeployEvent input, String apiGatewayQuery,
        Context context) throws IOException, URISyntaxException {
        createApiKeysTable();
        updateUrl();
        updateSwaggerHub();
        return new SimpleResponse("Success initializing resources.");

    }


    public void updateSwaggerHub()
        throws IOException, URISyntaxException {

        try{
            ObjectNode swaggerRoot = (ObjectNode) jsonParser.readTree(
                generateOpenApiSpecificationFromCode());
            Optional<ObjectNode> updatedSwaggerRootDoc =
                updateSwaggerRootWithServerInfoFromApiGateway(swaggerRoot);

            if (updatedSwaggerRootDoc.isPresent()) {

                SwaggerHubInfo swaggerHubInfo = new SwaggerHubInfo(apiId,
                    apiVersion,
                    swaggerOrganization);
                SwaggerDriver swaggerDriver = new SwaggerDriver(swaggerHubInfo);
                String apiKey = swaggerHubInfo.getSwaggerAuth();
                deletePreviousSwaggerHubSpecification(swaggerDriver, apiKey);
                updateSwaggerHubSpecification(updatedSwaggerRootDoc.get(), swaggerHubInfo, swaggerDriver);
            }
            else{
                logger.error("Could not generate SwaggerHub specification");
            }
        }
        catch(OpenApiConfigurationException e){
            logger.error(e.getMessage());
            throw new IOException(e);
        }


    }

    private void updateSwaggerHubSpecification(ObjectNode updatedSwaggerRootDoc,
        SwaggerHubInfo swaggerHubInfo, SwaggerDriver swaggerDriver)
        throws URISyntaxException, IOException {
        String swaggerString= Json.pretty(updatedSwaggerRootDoc);
        HttpPost updateRequest = swaggerDriver
            .createUpdateRequest(swaggerString,swaggerHubInfo.getSwaggerAuth());
        swaggerDriver.executePost(updateRequest);
    }

    private void deletePreviousSwaggerHubSpecification(SwaggerDriver swaggerDriver, String apiKey)
        throws URISyntaxException, IOException {
        HttpDelete deleteRequest = swaggerDriver.createDeleteApiRequest(apiKey);
        swaggerDriver.executeDelete(deleteRequest);
    }

    private String generateOpenApiSpecificationFromCode()
        throws OpenApiConfigurationException {
        OpenApiContext context = new JaxrsOpenApiContextBuilder().buildContext(true);
        return Json.pretty(context.read());
    }


    private Optional<ObjectNode> updateSwaggerRootWithServerInfoFromApiGateway(ObjectNode swaggerDocRoot)
        throws IOException {
        Optional<ServerInfo> serverInfo = readServerInfo();
        Optional<ObjectNode> newDoc = serverInfo
            .map(si -> updateSwaggerHubDocWithServerInfo(swaggerDocRoot, si));
        return newDoc;

    }


    @VisibleForTesting
    public ObjectNode updateSwaggerHubDocWithServerInfo(ObjectNode openApiDocRoot,
        ServerInfo serverInfo) {
        ArrayNode serversNode = serversNode(serverInfo);
        return (ObjectNode) openApiDocRoot.set(SERVERS_FIELD, serversNode);
    }


    private void createApiKeysTable() {
        try {
            authenticationService.createApiKeyTable();
            authenticationService.setUpInitialApiKeys();

        } catch (ResourceInUseException e) {
            logger.warn(e.getErrorMessage());
        }
    }


    private void updateUrl() {
        logger.debug("Updating URL.");
        UrlUpdater urlUpdater = createUrlUpdater();

        Optional<ChangeResourceRecordSetsRequest> request = urlUpdater
            .createUpdateRequest(certificateArn);
        List<String> changeList = request.map(req -> req.getChangeBatch().getChanges().stream())
            .orElse(Stream.empty())
            .map(Change::toString).collect(Collectors.toList());
        changeList.forEach(change -> logger.debug("Change:{}", change));
        ChangeResourceRecordSetsResult result = request
            .map(urlUpdater::executeUpdate)
            .orElseThrow(() -> new NotFoundException("Could not update Static URL settings"));
        logger.info(result.toString());

    }


    private Optional<ServerInfo> readServerInfo() throws IOException {
        String restApiId = restApiId(stackName);
        AmazonApiGateway apiGatewayClient = AmazonApiGatewayClientBuilder.defaultClient();
        ApiGatewayApiInfo apiGatewayApiInfo = new ApiGatewayApiInfo(stage,
            apiGatewayClient,
            restApiId);
        return apiGatewayApiInfo.readServerInfo();
    }


    private String restApiId(String stackName) {
        StackResources stackResources = new StackResources(stackName);
        String result = stackResources.getResourceIds(ResourceType.REST_API).stream().findAny()
            .orElseThrow(() -> new NotFoundException("RestApi not Found for stack:" + stackName));
        return result;
    }


    @VisibleForTesting
    public ArrayNode serversNode(ServerInfo serverInfo) {

        ArrayNode servers = jsonParser.createArrayNode();

        ObjectNode serverNode = jsonParser.createObjectNode();
        serverNode.put(URL_FIELD, serverInfo.getServerUrl());

        ObjectNode variablesNode = jsonParser.createObjectNode();
        ObjectNode basePathNode = jsonParser.createObjectNode();
        basePathNode.put(DEFAULT_FIELD, serverInfo.getStage());
        variablesNode.set(BASE_PATH_FIELD, basePathNode);
        serverNode.set(VARIABLES_FIELD, variablesNode);
        servers.add(serverNode);
        return servers;


    }


}
