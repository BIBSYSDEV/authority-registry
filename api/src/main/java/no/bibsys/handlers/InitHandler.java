package no.bibsys.handlers;

import com.amazonaws.services.apigateway.model.NotFoundException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsResult;
import java.util.Optional;
import no.bibsys.aws.lambda.events.DeployEvent;
import no.bibsys.aws.lambda.responses.SimpleResponse;
import no.bibsys.aws.tools.Environment;
import no.bibsys.service.AuthenticationService;
import no.bibsys.staticurl.UrlUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitHandler extends ResourceHandler {

    private final static Logger logger = LoggerFactory.getLogger(InitHandler.class);

    private final transient AuthenticationService authenticationService;



    public InitHandler() {
        this(new Environment());
    }

    public InitHandler(Environment environment){
        super(environment);
        final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        authenticationService = new AuthenticationService(client, environment);
    }


    @Override
    protected SimpleResponse processInput(DeployEvent input, String apiGatewayQuery,
        Context context) {
        logger.info("PROCESSING INPUT!");
        createApiKeysTable();
        updateUrl();

        return new SimpleResponse("Initializing!!");

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
        System.out.println("HELLOOOO");
        logger.info("Updating url!");
        UrlUpdater urlUpdater = createUrlUpdater();

        Optional<ChangeResourceRecordSetsRequest> request = urlUpdater
            .createUpdateRequest();
        ChangeResourceRecordSetsResult result = request
            .map(urlUpdater::executeUpdate)
            .orElseThrow(() -> new NotFoundException("Could not update Static URL settings"));
        logger.info(result.toString());

    }






}
