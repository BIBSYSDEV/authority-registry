package no.bibsys.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import no.bibsys.authorization.AuthPolicy;
import no.bibsys.authorization.PolicyDocument;
import no.bibsys.aws.tools.Environment;
import no.bibsys.service.ApiKey;
import no.bibsys.service.AuthenticationService;

public class AuthorizerHandler implements RequestHandler<Map<String, Object>, AuthPolicy> {


    private final transient AuthenticationService authenticationService;

    public AuthorizerHandler() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        Environment env = new Environment();
        authenticationService = new AuthenticationService(client, env);
    }


    public AuthorizerHandler(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public AuthPolicy handleRequest(Map<String, Object> input, Context context) {

        AuthInfo authInfo = new AuthInfo(input);

        @SuppressWarnings("unchecked")
        Map<String, String> headers = (Map<String, String>) input.get("headers");

        Optional<String> apiKeyInHeader = Optional.ofNullable((String) headers.get("api-key"));

        if (apiKeyInHeader.isPresent()) {

            ApiKey apiKey = authenticationService.getApiKey(apiKeyInHeader.get());

            System.out.println("Key: " + apiKey);

            String principalId = apiKey.getRole();
            String role = apiKey.getRole();
            String registry = apiKey.getRegistry();

            Map<String, String> responseContext = new ConcurrentHashMap<>();
            responseContext.put("role", role);
            responseContext.put("registry", registry);


            return new AuthPolicy(principalId,
                PolicyDocument.getAllowAllPolicy(principalId,authInfo.getRegion(), authInfo.getAwsAccountId(),
                    authInfo.getRestApiId(), authInfo.getStage()),
                responseContext);
        } else {
            System.err.println("No key");
            return new AuthPolicy("unauthorized_user",
                PolicyDocument.getDenyAllPolicy(authInfo.getRegion(), authInfo.getAwsAccountId(),
                    authInfo.getRestApiId(), authInfo.getStage()),
                new ConcurrentHashMap<>());
        }
    }


    public static class AuthInfo {

        private final transient String region;
        private final transient String awsAccountId;
        private final transient String restApiId;
        private final transient String stage;

        public AuthInfo(Map<String, Object> input) {
            String methodArn = (String) input.get("methodArn");
            String[] arnPartials = methodArn.split(":");
            this.region = arnPartials[3];

            this.awsAccountId = arnPartials[4];
            String[] apiGatewayArnPartials = arnPartials[5].split("/");
            this.restApiId = apiGatewayArnPartials[0];
            this.stage = apiGatewayArnPartials[1];
        }


        public String getRegion() {
            return region;
        }

        public String getAwsAccountId() {
            return awsAccountId;
        }

        public String getRestApiId() {
            return restApiId;
        }

        public String getStage() {
            return stage;
        }


    }


}
