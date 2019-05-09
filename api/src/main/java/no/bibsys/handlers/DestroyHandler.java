package no.bibsys.handlers;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.codepipeline.AWSCodePipeline;
import com.amazonaws.services.codepipeline.AWSCodePipelineClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsResult;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import no.bibsys.aws.lambda.events.DeployEvent;
import no.bibsys.aws.lambda.responses.SimpleResponse;
import no.bibsys.aws.secrets.SecretsReader;
import no.bibsys.aws.tools.Environment;
import no.bibsys.service.AuthenticationService;
import no.bibsys.staticurl.UrlUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DestroyHandler extends ResourceHandler {

    private static final Logger logger = LoggerFactory.getLogger(DestroyHandler.class);
    private static final String SUCESS_RESPONSE = "Destroying %s";
    private static final String DELETING_STACK_ERROR_MESSAGE = "Could not delete static URL for stack {}";

    private final transient AuthenticationService authenticationService;

    public DestroyHandler() {
        this(
            new Environment(),
            AWSCodePipelineClientBuilder.defaultClient(),
            initSwaggerHubSecretsBuilder(new Environment()),
            AmazonCloudFormationClientBuilder.defaultClient()
        );
    }

    public DestroyHandler(Environment environment, AWSCodePipeline codePipeline, SecretsReader swaggerHubsecretsReader,
        AmazonCloudFormation cloudFormation) {
        super(environment,
            codePipeline,
            swaggerHubsecretsReader,
            cloudFormation
        );

        final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        authenticationService = new AuthenticationService(client, new Environment());
    }

    @Override
    protected SimpleResponse processInput(DeployEvent inputObject, String apiGatewayQuery, Context context)
        throws IOException, URISyntaxException {
        String tableName = authenticationService.deleteApiKeyTable();
        deleteStaticUrl();
//        swaggerHubUpdater.deleteSwaggerHubApi();
        return new SimpleResponse(String.format(SUCESS_RESPONSE, tableName));
    }

    private void deleteStaticUrl() {
        UrlUpdater urlUpdater = createUrlUpdater();
        Optional<ChangeResourceRecordSetsRequest> deleteRequest = urlUpdater.createDeleteRequest();
        Optional<ChangeResourceRecordSetsResult> result = deleteRequest.map(urlUpdater::executeDelete);

        if (result.isPresent()) {
            logger.info(result.get().toString());
        } else {
            logger.warn(DELETING_STACK_ERROR_MESSAGE, stackName);
        }
    }
}
