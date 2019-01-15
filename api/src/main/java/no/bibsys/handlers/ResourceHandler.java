package no.bibsys.handlers;

import com.amazonaws.services.apigateway.AmazonApiGateway;
import com.amazonaws.services.apigateway.AmazonApiGatewayClientBuilder;
import com.amazonaws.services.apigateway.model.NotFoundException;
import no.bibsys.EnvironmentVariables;
import no.bibsys.aws.cloudformation.Stage;
import no.bibsys.aws.cloudformation.helpers.ResourceType;
import no.bibsys.aws.cloudformation.helpers.StackResources;
import no.bibsys.aws.lambda.handlers.templates.CodePipelineFunctionHandlerTemplate;
import no.bibsys.aws.lambda.responses.SimpleResponse;
import no.bibsys.aws.route53.Route53Updater;
import no.bibsys.aws.route53.StaticUrlInfo;
import no.bibsys.aws.tools.Environment;
import no.bibsys.staticurl.UrlUpdater;
import org.apache.commons.codec.digest.DigestUtils;


/**
 * Class for common methods of InitHandler and DestroyHandler.
 */
public abstract class ResourceHandler extends CodePipelineFunctionHandlerTemplate<SimpleResponse> {


    private final transient Stage stage;
    private final transient String hostedZoneName;
    private final transient String applicationUrl;

    protected final transient String stackName;
    private final transient String branch;


    public ResourceHandler(Environment environment) {
        super();
        this.hostedZoneName = environment.readEnv(EnvironmentVariables.HOSTED_ZONE_NAME);
        this.stage = Stage.fromString(environment.readEnv(EnvironmentVariables.STAGE_NAME));
        this.applicationUrl = environment.readEnv(EnvironmentVariables.APPLICATION_URL);
        this.stackName = environment.readEnv(EnvironmentVariables.STACK_NAME);
        this.branch = environment.readEnv(EnvironmentVariables.BRANCH);
    }

    protected UrlUpdater createUrlUpdater() {
        StaticUrlInfo urlInfo = initStaticUrlInfo(hostedZoneName, applicationUrl, stage, branch);

        String restApiId = restApiId();
        AmazonApiGateway apiGateway = AmazonApiGatewayClientBuilder.defaultClient();
        Route53Updater route53Updater = new Route53Updater(urlInfo, restApiId, apiGateway);

        return new UrlUpdater(route53Updater);
    }


    private String restApiId() {
        StackResources stackResources = new StackResources(stackName);
        return stackResources.getResourceIds(ResourceType.REST_API).stream().findAny()
            .orElseThrow(
                () -> new NotFoundException("Could not find a RestApi in stack " + stackName));
    }


    protected StaticUrlInfo initStaticUrlInfo(String hostedZoneName,
        String applicationUrl,
        Stage stage,
        String gitBranch) {

        StaticUrlInfo staticUrlInfo = new StaticUrlInfo(hostedZoneName, applicationUrl, stage);
        if (!GitConstants.MASTER_BRANCH.equalsIgnoreCase(gitBranch)) {

            String randomString = DigestUtils.sha1Hex(gitBranch).substring(0, 5);
            String newUrl = String.format("%s.%s", randomString, staticUrlInfo.getRecordSetName());
            staticUrlInfo = new StaticUrlInfo(hostedZoneName, newUrl,
                staticUrlInfo.getStage());
        }
        if (Stage.TEST.equals(stage)) {
            String newUrl = "test." + staticUrlInfo.getRecordSetName();
            staticUrlInfo = new StaticUrlInfo(staticUrlInfo.getZoneName(),
                newUrl,
                staticUrlInfo.getStage());
        }
        return staticUrlInfo;

    }


}
