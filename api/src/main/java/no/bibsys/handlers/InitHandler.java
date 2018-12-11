package no.bibsys.handlers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.bibsys.EnvironmentVariables;
import no.bibsys.aws.apigateway.ApiGatewayApiInfo;
import no.bibsys.aws.cloudformation.Stage;
import no.bibsys.aws.cloudformation.helpers.ResourceType;
import no.bibsys.aws.cloudformation.helpers.StackResources;
import no.bibsys.aws.lambda.events.DeployEvent;
import no.bibsys.aws.lambda.responses.SimpleResponse;
import no.bibsys.aws.swaggerhub.SwaggerDriver;
import no.bibsys.aws.swaggerhub.SwaggerHubInfo;
import no.bibsys.aws.tools.Environment;
import no.bibsys.aws.tools.JsonUtils;
import no.bibsys.service.AuthenticationService;
import no.bibsys.staticurl.UrlUpdater;

public class InitHandler extends ResourceHandler {

    private final static Logger logger = LoggerFactory.getLogger(InitHandler.class);

    private final transient AuthenticationService authenticationService;
    private final transient String certificateArn;
    
    private final transient String apiId;
    private final transient String apiVersion;
    private final transient String swaggerOrganization;
    private final transient String stackName;
    private final transient String stageName;
    
    public InitHandler() {
        this(new Environment());

    }

    public InitHandler(Environment environment){
        super(environment);
        final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        this.certificateArn = environment.readEnv(EnvironmentVariables.CERTIFICATE_ARN_ENV);
        this.apiId = environment.readEnv(EnvironmentVariables.SWAGGER_API_ID);
        this.apiVersion = environment.readEnv(EnvironmentVariables.SWAGGER_API_VERSION);
        this.swaggerOrganization = environment.readEnv(EnvironmentVariables.SWAGGER_API_OWNER);
        this.stackName = environment.readEnv(EnvironmentVariables.STACK_NAME);
        this.stageName = environment.readEnv(EnvironmentVariables.STAGE_NAME);
        authenticationService = new AuthenticationService(client, environment);
    }


    @Override
    protected SimpleResponse processInput(DeployEvent input, String apiGatewayQuery,
        Context context) {

        createApiKeysTable();
        updateUrl();
        createSwaggerHubApi();
        return new SimpleResponse("Success initializing resources.");

    }

    private void createSwaggerHubApi() {
                
        SwaggerHubInfo swaggerHubInfo = new SwaggerHubInfo(apiId, apiVersion, swaggerOrganization);  
        SwaggerDriver swaggerDriver = new SwaggerDriver(swaggerHubInfo);
        AmazonApiGateway apiGatewayClient = AmazonApiGatewayClientBuilder.defaultClient();
        String restApiId = restApiId(stackName);
        Stage stage = Stage.fromString(stageName);
        ApiGatewayApiInfo apiGatewayApiInfo = new ApiGatewayApiInfo(stage, apiGatewayClient, restApiId);
        
        try {
            
            Map<String, String> requestParameters = new ConcurrentHashMap<>();
            requestParameters.put("accepts", "application/json");
            Optional<JsonNode> amazonApiSpec = apiGatewayApiInfo.readOpenApiSpecFromAmazon(requestParameters);
            
            if (amazonApiSpec.isPresent()) {
    
                ObjectMapper mapper = JsonUtils.newJsonParser();
                String jsonSpec = mapper.writeValueAsString(amazonApiSpec.get());
                String apiKey = swaggerHubInfo.getSwaggerAuth();
                swaggerDriver.createUpdateRequest(jsonSpec, apiKey);
              
            } else {
                logger.warn("No swagger specification");
            }
        
        } catch (Exception e) {
            logger.error("SwaggerHub error", e.getMessage());
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
        List<String> changeList= request.map(req -> req.getChangeBatch().getChanges().stream())
            .orElse(Stream.empty())
            .map(Change::toString).collect(Collectors.toList());
        changeList.forEach(change->logger.debug("Change:{}",change));
        ChangeResourceRecordSetsResult result = request
            .map(urlUpdater::executeUpdate)
            .orElseThrow(() -> new NotFoundException("Could not update Static URL settings"));
        logger.info(result.toString());

    }








}
