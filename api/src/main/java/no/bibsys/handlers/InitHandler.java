package no.bibsys.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.lambda.runtime.Context;
import no.bibsys.EnvironmentReader;
import no.bibsys.amazon.handlers.events.buildevents.BuildEvent;
import no.bibsys.amazon.handlers.responses.SimpleResponse;
import no.bibsys.amazon.handlers.templates.CodePipelineFunctionHandlerTemplate;
import no.bibsys.service.AuthenticationService;

public class InitHandler extends CodePipelineFunctionHandlerTemplate<SimpleResponse> {

    private final transient AuthenticationService authenticationService;

    public InitHandler() {
        super();
        
        final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        authenticationService = new AuthenticationService(client, new EnvironmentReader());
    }


    @Override
    public SimpleResponse processInput(BuildEvent input, Context context) {

        try {
            authenticationService.createApiKeyTable();
            authenticationService.setUpInitialApiKeys();
        } catch (ResourceInUseException e) {
            logger.log(e.getErrorMessage());
        }

        return new SimpleResponse("Initializing!!");

    }

}