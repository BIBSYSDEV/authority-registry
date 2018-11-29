package no.bibsys.handlers;

import com.amazonaws.services.apigateway.AmazonApiGateway;
import com.amazonaws.services.apigateway.AmazonApiGatewayClientBuilder;
import com.amazonaws.services.apigateway.model.NotFoundException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsResult;
import java.util.Optional;
import no.bibsys.EnvironmentReader;
import no.bibsys.aws.cloudformation.Stage;
import no.bibsys.aws.cloudformation.helpers.ResourceType;
import no.bibsys.aws.cloudformation.helpers.StackResources;
import no.bibsys.aws.lambda.events.DeployEvent;
import no.bibsys.aws.lambda.handlers.templates.CodePipelineFunctionHandlerTemplate;
import no.bibsys.aws.lambda.responses.SimpleResponse;
import no.bibsys.aws.route53.Route53Updater;
import no.bibsys.aws.route53.StaticUrlInfo;
import no.bibsys.aws.tools.Environment;
import no.bibsys.service.AuthenticationService;
import no.bibsys.staticurl.UrlUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitHandler extends CodePipelineFunctionHandlerTemplate<SimpleResponse> {

    private final static Logger logger = LoggerFactory.getLogger(InitHandler.class);
    private static final String STACK_NAME = "STACK_NAME";

    private static String CERTIFICATE_ARN_ENV = "REGIONAL_CERTIFICATE_ARN";
    private static String HOSTED_ZONE_NAME = "HOSTED_ZONE_NAME";
    private static String APPLICATION_URL = "APPLICATION_URL";



    private final transient AuthenticationService authenticationService;
    private final transient Stage stage;
    private final transient String certificateArn;
    private final transient String hostedZoneName;
    private final transient String applicationUrl;
    private final transient String stackName;

    public InitHandler() {
        super();
        Environment environment = new Environment();
        certificateArn = environment.readEnv(CERTIFICATE_ARN_ENV);
        hostedZoneName = environment.readEnv(HOSTED_ZONE_NAME);
        stage = Stage.fromString(environment.readEnv(EnvironmentReader.STAGE_NAME));
        applicationUrl = environment.readEnv(APPLICATION_URL);
        this.stackName = environment.readEnv(STACK_NAME);
        final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        authenticationService = new AuthenticationService(client, new EnvironmentReader());
    }


    @Override
    protected SimpleResponse processInput(DeployEvent input, String apiGatewayQuery,
        Context context) {
        logger.info("PROCESSING INPUT!");
        createApiKeysTable();
        updateUrl();

        return new SimpleResponse("Initializing!!");

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
        System.out.println("HELLOOOO");
        logger.info("Updating url!");
        StaticUrlInfo urlInfo = new StaticUrlInfo(hostedZoneName, applicationUrl, stage);
        String restApiId = restApiId();
        AmazonApiGateway apiGateway = AmazonApiGatewayClientBuilder.defaultClient();
        Route53Updater route53Updater = new Route53Updater(urlInfo, restApiId, apiGateway);
        UrlUpdater urlUpdater = new UrlUpdater(route53Updater, certificateArn);

        Optional<ChangeResourceRecordSetsRequest> request = urlUpdater
            .createUpdateRequest();
        ChangeResourceRecordSetsResult result = request
            .map(urlUpdater::executeUpdate)
            .orElseThrow(() -> new NotFoundException("Could not update Static URL settings"));
        logger.info(result.toString());

    }


    private String restApiId() {
        StackResources stackResources = new StackResources(stackName);
        return stackResources.getResourceIds(ResourceType.REST_API).stream().findAny()
            .orElseThrow(
                () -> new NotFoundException("Could not find a RestApi in stack " + stackName));
    }


}
