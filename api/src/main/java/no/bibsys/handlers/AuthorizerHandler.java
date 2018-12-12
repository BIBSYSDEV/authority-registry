package no.bibsys.handlers;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import no.bibsys.aws.tools.Environment;
import no.bibsys.handlers.AuthPolicy.PolicyDocument;
import no.bibsys.service.ApiKey;
import no.bibsys.service.AuthenticationService;

public class AuthorizerHandler implements RequestHandler<Map<String, Object>, AuthPolicy> {

    private final transient AuthenticationService authenticationService;


    public AuthorizerHandler() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        Environment env = new Environment();
        authenticationService = new AuthenticationService(client, env);
    }

    @Override
    public AuthPolicy handleRequest(Map<String, Object> input, Context context) {

        String methodArn = (String)input.get("methodArn");
        String[] arnPartials = methodArn.split(":");
        String region = arnPartials[3];
        String awsAccountId = arnPartials[4];
        String[] apiGatewayArnPartials = arnPartials[5].split("/");
        String restApiId = apiGatewayArnPartials[0];
        String stage = apiGatewayArnPartials[1];
        
        @SuppressWarnings("unchecked")
        Map<String,String> headers = (Map<String,String>)input.get("headers");
        
        Optional<String> apiKeyInHeader = Optional.ofNullable((String) headers.get("api-key"));
        
        if (apiKeyInHeader.isPresent()) {           
            
            ApiKey apiKey = authenticationService.getApiKey(apiKeyInHeader.get());
            
            System.out.println("Key: " + apiKey);
            
            String principalId = apiKey.getRole();
            String role = apiKey.getRole();
            String registry = apiKey.getRegistry();
            
            Map<String,String> responseContext = new ConcurrentHashMap<>();
            responseContext.put("role", role);
            responseContext.put("registry", registry);
        
            return new AuthPolicy(principalId, PolicyDocument.getAllowAllPolicy(region, awsAccountId, restApiId, stage), responseContext);
        } else {
            
            System.err.println("No key");
            
            return new AuthPolicy("unauthorized_user", PolicyDocument.getDenyAllPolicy(region, awsAccountId, restApiId, stage), new ConcurrentHashMap<>());
        }
    }


}
