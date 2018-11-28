package no.bibsys.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.lambda.runtime.Context;
import java.io.IOException;
import java.net.URISyntaxException;
import no.bibsys.EnvironmentReader;
import no.bibsys.aws.lambda.events.DeployEvent;
import no.bibsys.aws.lambda.handlers.templates.CodePipelineFunctionHandlerTemplate;
import no.bibsys.aws.lambda.responses.SimpleResponse;
import no.bibsys.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitHandler extends CodePipelineFunctionHandlerTemplate<SimpleResponse> {

    private final static Logger logger = LoggerFactory.getLogger(InitHandler.class);

    private final transient AuthenticationService authenticationService;


    public InitHandler() {
        super();

        final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        authenticationService = new AuthenticationService(client, new EnvironmentReader());
    }


    @Override
    protected SimpleResponse processInput(DeployEvent input, String apiGatewayQuery,
        Context context)
        throws IOException, URISyntaxException {

        try {
            authenticationService.createApiKeyTable();
            authenticationService.setUpInitialApiKeys();
        } catch (ResourceInUseException e) {
            logger.warn(e.getErrorMessage());
        }

        return new SimpleResponse("Initializing!!");

    }


}
