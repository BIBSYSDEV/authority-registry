package no.bibsys.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import java.io.IOException;
import java.net.URISyntaxException;
import no.bibsys.EnvironmentReader;
import no.bibsys.aws.lambda.events.DeployEvent;
import no.bibsys.aws.lambda.handlers.templates.CodePipelineFunctionHandlerTemplate;
import no.bibsys.aws.lambda.responses.SimpleResponse;
import no.bibsys.service.AuthenticationService;

public class DestroyHandler extends CodePipelineFunctionHandlerTemplate<SimpleResponse> {

    private final transient AuthenticationService authenticationService;


    public DestroyHandler() {
        super();

        final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        authenticationService = new AuthenticationService(client, new EnvironmentReader());
    }

    @Override
    protected SimpleResponse processInput(DeployEvent inputObject, String apiGatewayQuery,
        Context context) throws IOException, URISyntaxException {
        authenticationService.deleteApiKeyTable();

        return new SimpleResponse("Destroying!!");
    }
}
