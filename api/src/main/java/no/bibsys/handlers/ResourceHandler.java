package no.bibsys.handlers;

import com.amazonaws.services.apigateway.AmazonApiGateway;
import com.amazonaws.services.apigateway.AmazonApiGatewayClientBuilder;
import com.amazonaws.services.apigateway.model.NotFoundException;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.codepipeline.AWSCodePipeline;
import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.AmazonRoute53ClientBuilder;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import no.bibsys.EnvironmentVariables;
import no.bibsys.aws.cloudformation.Stage;
import no.bibsys.aws.cloudformation.helpers.ResourceType;
import no.bibsys.aws.cloudformation.helpers.StackResources;
import no.bibsys.aws.lambda.handlers.templates.CodePipelineCommunicator;
import no.bibsys.aws.lambda.handlers.templates.CodePipelineFunctionHandlerTemplate;
import no.bibsys.aws.lambda.responses.SimpleResponse;
import no.bibsys.aws.route53.Route53Updater;
import no.bibsys.aws.route53.StaticUrlInfo;
import no.bibsys.aws.secrets.AwsSecretsReader;
import no.bibsys.aws.secrets.SecretsReader;
import no.bibsys.aws.tools.Environment;
import no.bibsys.handlers.utils.SwaggerHubUpdater;
import no.bibsys.staticurl.UrlUpdater;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for common methods of InitHandler and DestroyHandler.
 */
public abstract class ResourceHandler extends CodePipelineFunctionHandlerTemplate<SimpleResponse> {

    private static final String REST_API_NOT_FOUND_MESSAGE = "Could not find a RestApi in stack ";
    private static final Logger logger = LoggerFactory.getLogger(ResourceHandler.class);

    protected final transient String stackName;
    protected final transient SwaggerHubUpdater swaggerHubUpdater;
    private final transient Stage stage;
    private final transient String hostedZoneName;
    private final transient String applicationUrl;
    private final transient String branch;
    private final transient AmazonCloudFormation cloudFormation;

    public ResourceHandler(Environment environment,
                           AWSCodePipeline codePipeline,
                           SecretsReader swaggerHubSecretsReader,
                           AmazonCloudFormation cloudFormation
    ) {
        super(new CodePipelineCommunicator(codePipeline));
        this.hostedZoneName = environment.readEnv(EnvironmentVariables.HOSTED_ZONE_NAME);
        this.stage = Stage.fromString(environment.readEnv(EnvironmentVariables.STAGE_NAME));
        this.applicationUrl = environment.readEnv(EnvironmentVariables.APPLICATION_URL);
        this.stackName = environment.readEnv(EnvironmentVariables.STACK_NAME);
        this.branch = environment.readEnv(EnvironmentVariables.BRANCH);
        this.cloudFormation = cloudFormation;
        String apiId = environment.readEnv(EnvironmentVariables.SWAGGER_API_ID);
        String apiVersion = environment.readEnv(EnvironmentVariables.SWAGGER_API_VERSION);
        String swaggerOrganization = environment.readEnv(EnvironmentVariables.SWAGGER_API_OWNER);

        this.swaggerHubUpdater = new SwaggerHubUpdater(apiId, apiVersion, swaggerOrganization, stackName, stage,
            branch, swaggerHubSecretsReader, cloudFormation);
    }

    protected static SecretsReader initSwaggerHubSecretsBuilder(Environment environment) {

        String swaggerApiKeySecretName = environment.readEnv(EnvironmentVariables.SWAGGERHUB_API_KEY_SECRET_NAME);
        String swaggerApiKeySecretKey = environment.readEnv(EnvironmentVariables.SWAGGERHUB_API_KEY_SECRET_KEY);

        return new AwsSecretsReader(
                AWSSecretsManagerClientBuilder.defaultClient(),
                swaggerApiKeySecretName, swaggerApiKeySecretKey);
    }

    protected UrlUpdater createUrlUpdater() {
        StaticUrlInfo urlInfo = initStaticUrlInfo(hostedZoneName, applicationUrl, stage, branch);

        String restApiId = restApiId();
        AmazonApiGateway apiGateway = AmazonApiGatewayClientBuilder.defaultClient();
        AmazonRoute53 route53Client = AmazonRoute53ClientBuilder.defaultClient();
        Route53Updater route53Updater = new Route53Updater(urlInfo, restApiId, apiGateway, route53Client);
        return new UrlUpdater(route53Updater);
    }

    private String restApiId() {
        StackResources stackResources = new StackResources(stackName, cloudFormation);
        return stackResources.getResourceIds(ResourceType.REST_API).stream().findAny()
                .orElseThrow(() -> new NotFoundException(String.join(" ", REST_API_NOT_FOUND_MESSAGE, stackName)));
    }

    protected StaticUrlInfo initStaticUrlInfo(String hostedZoneName, String applicationUrl, Stage stage,
                                              String gitBranch) {

        StaticUrlInfo staticUrlInfo = new StaticUrlInfo(hostedZoneName, applicationUrl, stage);
        if (!GitConstants.MASTER_BRANCH.equalsIgnoreCase(gitBranch)) {

            String randomString = DigestUtils.sha1Hex(gitBranch).substring(0, 5);
            String newUrl = String.format("%s-%s", randomString, staticUrlInfo.getRecordSetName());
            logger.info("URLLLLLLLLLLLLL:"+newUrl);
            staticUrlInfo = new StaticUrlInfo(hostedZoneName, newUrl, staticUrlInfo.getStage());
        }
        if (Stage.TEST.equals(stage)) {
            String newUrl = "test-" + staticUrlInfo.getRecordSetName();
            logger.info("TESTING URLLLLLLLLLLLLL:"+newUrl);
            staticUrlInfo = new StaticUrlInfo(staticUrlInfo.getZoneName(), newUrl, staticUrlInfo.getStage());
        }
        return staticUrlInfo;
    }
}
