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

public abstract class ResourceHandler extends CodePipelineFunctionHandlerTemplate<SimpleResponse> {

    public static final String STACK_NAME = "STACK_NAME";
    public static final String CERTIFICATE_ARN_ENV = "REGIONAL_CERTIFICATE_ARN";
    public static final String HOSTED_ZONE_NAME = "HOSTED_ZONE_NAME";
    public static final String APPLICATION_URL = "APPLICATION_URL";

    private final transient Stage stage;
    private final transient String certificateArn;
    private final transient String hostedZoneName;
    private final transient String applicationUrl;

    protected final transient String stackName;


    public ResourceHandler(Environment environment) {
        super();
        certificateArn = environment.readEnv(CERTIFICATE_ARN_ENV);
        hostedZoneName = environment.readEnv(HOSTED_ZONE_NAME);
        stage = Stage.fromString(environment.readEnv(EnvironmentVariables.STAGE_NAME));
        applicationUrl = environment.readEnv(APPLICATION_URL);
        this.stackName = environment.readEnv(STACK_NAME);

    }


    protected UrlUpdater createUrlUpdater() {
        StaticUrlInfo urlInfo = new StaticUrlInfo(hostedZoneName, applicationUrl, stage);
        String restApiId = restApiId();
        AmazonApiGateway apiGateway = AmazonApiGatewayClientBuilder.defaultClient();
        Route53Updater route53Updater = new Route53Updater(urlInfo, restApiId, apiGateway);

        return new UrlUpdater(route53Updater, certificateArn);
    }


    private String restApiId() {
        StackResources stackResources = new StackResources(stackName);
        return stackResources.getResourceIds(ResourceType.REST_API).stream().findAny()
            .orElseThrow(
                () -> new NotFoundException("Could not find a RestApi in stack " + stackName));
    }


}
