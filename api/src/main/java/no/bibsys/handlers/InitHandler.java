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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import no.bibsys.aws.tools.IoUtils;
import no.bibsys.aws.tools.JsonUtils;
import no.bibsys.service.AuthenticationService;
import no.bibsys.staticurl.UrlUpdater;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitHandler extends ResourceHandler {

    public static final String BASE_PATH_FIELD = "basePath";
    public static final String OPENAPI_FOLDER = "openapi";
    public static final String OPENAPI_YAML = "openapi.yaml";
    public static final String URL_FIELD = "url";
    public static final String DEFAULT_FIELD = "default";
    public static final String VARIABLES_FIELD = "variables";
    public static final String SERVERS_FIELD = "servers";
    private static final String BUILD_FOLDER = "build";
    private final static Logger logger = LoggerFactory.getLogger(InitHandler.class);
    private final transient AuthenticationService authenticationService;
    private final transient String certificateArn;

    private final transient String apiId;
    private final transient String apiVersion;
    private final transient String swaggerOrganization;
    private final transient String stackName;
    private final transient Stage stage;

    private final transient ObjectMapper yamlParser = JsonUtils.newYamlParser();

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
//        updateSwaggerHub();
        return new SimpleResponse("Success initializing resources.");

    }


    public void updateSwaggerHub()
        throws IOException, URISyntaxException, OpenApiConfigurationException {
        String swaggerString = generateOpenApiSpecification();
        SwaggerHubInfo swaggerHubInfo = new SwaggerHubInfo(apiId, apiVersion, swaggerOrganization);
        SwaggerDriver swaggerDriver = new SwaggerDriver(swaggerHubInfo);
        String apiKey = swaggerHubInfo.getSwaggerAuth();
        HttpDelete deleteRequest = swaggerDriver
            .createDeleteApiRequest(apiKey);
        swaggerDriver.executeDelete(deleteRequest);
        HttpPost updateRequest = swaggerDriver
                .createUpdateRequest(swaggerString, swaggerHubInfo.getSwaggerAuth());
            swaggerDriver.executePost(updateRequest);
    }

    private String generateOpenApiSpecification()
        throws OpenApiConfigurationException {
        OpenApiContext context = new JaxrsOpenApiContextBuilder().buildContext(true);
        return Yaml.pretty(context.read());
    }

    private Optional<String> createSwaggerJsonString() throws IOException {
        Optional<ServerInfo> serverInfo = readServerInfo();
        ObjectNode openApiDocRoot = readLocalSwaggerFile();
        return serverInfo
            .map(si -> updateSwaggerHubDocWithServerInfo(openApiDocRoot, si))
            .flatMap(docRoot -> nodeAsJsonString(docRoot));
    }


    private Optional<String> nodeAsJsonString(ObjectNode node) {
        try {
            return Optional.of(JsonUtils.newJsonParser().writeValueAsString(node));
        } catch (JsonProcessingException e) {
            logger.error("Failed to write ObjectNode to YAML string");
            return Optional.empty();
        }
    }

    private String restApiId(String stackName) {
        StackResources stackResources = new StackResources(stackName);
        String result = stackResources.getResourceIds(ResourceType.REST_API).stream().findAny()
            .orElseThrow(() -> new NotFoundException("RestApi not Found for stack:" + stackName));
        return result;
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


    public ObjectNode updateSwaggerHubDocWithServerInfo(ObjectNode openApiDocRoot,
        ServerInfo serverInfo) {

        ArrayNode serversNode = serversNode(serverInfo);
        ObjectNode newApiObjectDoc = (ObjectNode) openApiDocRoot.set(SERVERS_FIELD, serversNode);
        return newApiObjectDoc;
    }

    private Optional<ServerInfo> readServerInfo() throws IOException {
        String restApiId = restApiId(stackName);
        AmazonApiGateway apiGatewayClient = AmazonApiGatewayClientBuilder.defaultClient();
        ApiGatewayApiInfo apiGatewayApiInfo = new ApiGatewayApiInfo(stage,
            apiGatewayClient,
            restApiId);
        return apiGatewayApiInfo.readServerInfo();
    }


    public ArrayNode serversNode(ServerInfo serverInfo) {
        ArrayNode servers = yamlParser.createArrayNode();

        ObjectNode serverNode = yamlParser.createObjectNode();
        serverNode.put(URL_FIELD, serverInfo.getServerUrl());

        ObjectNode variablesNode = yamlParser.createObjectNode();
        ObjectNode basePathNode = yamlParser.createObjectNode();
        basePathNode.put(DEFAULT_FIELD, serverInfo.getStage());
        variablesNode.set(BASE_PATH_FIELD, basePathNode);
        serverNode.set(VARIABLES_FIELD, variablesNode);
        servers.add(serverNode);
        return servers;


    }


    public ObjectNode readLocalSwaggerFile() throws IOException {
        Path path = Paths.get(BUILD_FOLDER, OPENAPI_FOLDER, OPENAPI_YAML);
        return readFile(path).flatMap(this::parseYamlFile)
            .orElseThrow(() -> new IOException("Could not read or parse swagger file"));


    }


    private Optional<String> readFile(Path path) throws IOException {
        try {
            String contents = IoUtils.fileAsString(path);
            if (StringUtils.isNotEmpty(contents)) {
                return Optional.ofNullable(contents);
            } else {
                return Optional.empty();
            }

        } catch (IOException e) {
            logger.warn("Could not find Swagger file in " + path.toString());
            return Optional.empty();
        }
    }

    private Optional<ObjectNode> parseYamlFile(String openApiString) {
        try {
            return Optional
                .of(yamlParser.readTree(openApiString))
                .filter(root -> root instanceof ObjectNode)
                .map(root -> (ObjectNode) root);

        } catch (IOException e) {
            logger.warn("Error parsing Swagger file.");
            return Optional.empty();
        }

    }
}
