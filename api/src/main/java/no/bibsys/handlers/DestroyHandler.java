package no.bibsys.handlers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsResult;

import no.bibsys.aws.lambda.events.DeployEvent;
import no.bibsys.aws.lambda.responses.SimpleResponse;
import no.bibsys.aws.tools.Environment;
import no.bibsys.service.AuthenticationService;
import no.bibsys.staticurl.UrlUpdater;

public class DestroyHandler extends ResourceHandler {


    private static final Logger logger = LoggerFactory.getLogger(DestroyHandler.class);

    private final transient AuthenticationService authenticationService;


    public DestroyHandler() {
        this(new Environment());
    }

    public DestroyHandler(Environment environment) {
        super(environment);

        final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        authenticationService = new AuthenticationService(client, new Environment());

    }

    @Override
    protected SimpleResponse processInput(DeployEvent inputObject, String apiGatewayQuery,
        Context context) throws IOException, URISyntaxException {
        authenticationService.deleteApiKeyTable();
        deleteStaticUrl();
        return new SimpleResponse("Destroying!!");
    }



    private void deleteStaticUrl() {
        UrlUpdater urlUpdater = createUrlUpdater();
        Optional<ChangeResourceRecordSetsRequest> deleteRequest = urlUpdater
            .createDeleteRequest();
        Optional<ChangeResourceRecordSetsResult> result = deleteRequest
            .map(urlUpdater::executeDelete);

        if (result.isPresent()) {
            logger.info(result.get().toString());
        } else {
            logger.warn("Could not delete static URL for stack {}",stackName);
        }

    }
}
