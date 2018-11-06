package no.bibsys.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import no.bibsys.EnvironmentReader;
import no.bibsys.service.AuthenticationService;

public class DestroyLambdaHandler implements RequestHandler<String, String> {

    private final transient AuthenticationService authenticationService;
    
    public DestroyLambdaHandler() {
        final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        authenticationService = new AuthenticationService(client, new EnvironmentReader());
    }
    
    @Override
    public String handleRequest(String input, Context context) {
        
        return authenticationService.deleteApiKeyTable();
    }
    
}
