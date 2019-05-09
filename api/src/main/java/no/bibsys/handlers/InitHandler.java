package no.bibsys.handlers;

import com.amazonaws.services.apigateway.model.NotFoundException;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.codepipeline.AWSCodePipelineClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.route53.model.Change;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsResult;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import no.bibsys.EnvironmentVariables;
import no.bibsys.aws.lambda.events.DeployEvent;
import no.bibsys.aws.lambda.responses.SimpleResponse;
import no.bibsys.aws.tools.Environment;
import no.bibsys.service.AuthenticationService;
import no.bibsys.staticurl.UrlUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitHandler extends ResourceHandler {

    private static final Logger logger = LoggerFactory.getLogger(InitHandler.class);

    private static final String SUCCESS_MESSAGE = "Success initializing resources.";
    private static final String UPDATING_URL_LOGGER_DEBUG = "Could not update Static URL settings";
    private static final String CHANGE_DEBUG_MESSAGE = "Change:{}";

    private final transient AuthenticationService authenticationService;
    private final transient String certificateArn;

    public InitHandler() {
        this(new Environment());
    }

    public InitHandler(Environment environment) {
        super(environment,
            AWSCodePipelineClientBuilder.defaultClient(),
            initSwaggerHubSecretsBuilder(environment),
            AmazonCloudFormationClientBuilder.defaultClient()
        );
        final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        this.certificateArn = environment.readEnv(EnvironmentVariables.CERTIFICATE_ARN_ENV);

        authenticationService = new AuthenticationService(client, environment);
    }

    @Override
    protected SimpleResponse processInput(DeployEvent input, String apiGatewayQuery, Context context)
        throws IOException, URISyntaxException {
//        createApiKeysTable();
//        updateUrl();
//        swaggerHubUpdater.updateSwaggerHub();
        return new SimpleResponse(SUCCESS_MESSAGE);
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
        UrlUpdater urlUpdater = createUrlUpdater();

        Optional<ChangeResourceRecordSetsRequest> request = urlUpdater.createUpdateRequest(certificateArn);
        List<String> changeList = request.map(req -> req.getChangeBatch().getChanges().stream()).orElse(Stream.empty())
            .map(Change::toString).collect(Collectors.toList());
        changeList.forEach(change -> logger.debug(CHANGE_DEBUG_MESSAGE, change));
        ChangeResourceRecordSetsResult result = request.map(urlUpdater::executeUpdate)
            .orElseThrow(() -> new NotFoundException(UPDATING_URL_LOGGER_DEBUG));
        logger.info(result.toString());
    }
}
