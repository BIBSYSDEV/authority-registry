package no.bibsys.handlers;

import java.util.ArrayList;
import java.util.List;
import com.amazonaws.Request;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.SSESpecification;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import no.bibsys.EnvironmentReader;
import no.bibsys.web.security.ApiKeyConstants;

public class InitLambdaHandler implements RequestHandler<String, String> {

    private final transient String apiKeyTableName;
    private final transient DynamoDB dynamoDB;
    
    public InitLambdaHandler() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        dynamoDB = new DynamoDB(client);
        apiKeyTableName = new EnvironmentReader().getEnvForName(ApiKeyConstants.API_KEY_TABLE_NAME).orElse("entity-registry-api-keys");
    }
    
    
    
    @Override
    public String handleRequest(String input, Context context) {
        List<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("Key").withAttributeType("S"));

        List<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
        keySchema.add(new KeySchemaElement().withAttributeName("Key").withKeyType(KeyType.HASH));
        
        CreateTableRequest request = new CreateTableRequest();
        request
        .withTableName(apiKeyTableName)
        .withKeySchema(keySchema)
        .withAttributeDefinitions(attributeDefinitions)
        .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L))
        .withSSESpecification(new SSESpecification().withEnabled(true));
        
        Table table = dynamoDB.createTable(request);
        try {
            table.waitForActive();
        } catch (InterruptedException e) {

        }
        
        return table.getTableName();

    }

}
