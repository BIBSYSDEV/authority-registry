package no.bibsys.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import java.io.IOException;
import java.net.URISyntaxException;
import no.bibsys.EnvironmentReader;
import no.bibsys.amazon.handlers.events.CodePipelineEvent;
import no.bibsys.amazon.handlers.responses.SimpleResponse;
import no.bibsys.amazon.handlers.templates.CodePipelineFunctionHandlerTemplate;
import no.bibsys.service.AuthenticationService;

public class InitHandler extends CodePipelineFunctionHandlerTemplate<SimpleResponse> {


    private final transient AuthenticationService authenticationService;

    public InitHandler() {
        final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        authenticationService = new AuthenticationService(client, new EnvironmentReader());
    }



    @Override
    public SimpleResponse processInput(CodePipelineEvent input, Context context)
        throws IOException, URISyntaxException {

        String pipelineId = input.getId();

        String tableName = authenticationService.createApiKeyTable();
        authenticationService.setUpInitialApiKeys();

        return new SimpleResponse(tableName);


    }


}
