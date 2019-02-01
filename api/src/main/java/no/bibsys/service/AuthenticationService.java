package no.bibsys.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.SSESpecification;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import no.bibsys.EnvironmentVariables;
import no.bibsys.aws.cloudformation.Stage;
import no.bibsys.aws.tools.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final transient DynamoDBMapper mapper;
    private final transient DynamoDBMapperConfig config;
    private final transient Environment environmentReader;
    private final transient String apiKeyTableName;
    private final transient DynamoDB dynamoDB;


    public AuthenticationService(AmazonDynamoDB client, Environment environmentReader) {
        this.environmentReader = environmentReader;
        mapper = new DynamoDBMapper(client);
        this.dynamoDB = new DynamoDB(client);

        apiKeyTableName = environmentReader.readEnv(EnvironmentVariables.API_KEY_TABLE_NAME);

        config = DynamoDBMapperConfig.builder()
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(apiKeyTableName))
                .build();
    }

    public ApiKey getApiKey(String apiKeyInHeader) {
        ApiKey apiKey = mapper.load(ApiKey.class, apiKeyInHeader, config);
        if (Objects.isNull(apiKey)) {
            apiKey = new ApiKey();
        }
        return apiKey;
    }

    public String createApiKeyTable() {
        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions
                .add(new AttributeDefinition().withAttributeName("Key").withAttributeType("S"));

        List<KeySchemaElement> keySchema = new ArrayList<>();
        keySchema.add(new KeySchemaElement().withAttributeName("Key").withKeyType(KeyType.HASH));

        CreateTableRequest request = new CreateTableRequest();
        request.withTableName(apiKeyTableName).withKeySchema(keySchema)
                .withAttributeDefinitions(attributeDefinitions)
                .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L)
                        .withWriteCapacityUnits(1L))
                .withSSESpecification(new SSESpecification().withEnabled(true));

        Table table = dynamoDB.createTable(request);
        try {
            table.waitForActive();
        } catch (InterruptedException e) {
            logger.warn(e.getMessage());
        }

        return table.getTableName();
    }

    public void setUpInitialApiKeys() {
        Stage currentStage = Stage.fromString(environmentReader.readEnv(EnvironmentVariables.STAGE_NAME));
        if (currentStage.equals(Stage.TEST)) {
            ApiKey apiAdminApiKey = ApiKey.createApiAdminApiKey();
            apiAdminApiKey.setKey("testApiAdminApiKey");
            saveApiKey(apiAdminApiKey);
        } else {
            saveApiKey(ApiKey.createApiAdminApiKey());
        }
    }

    public String saveApiKey(ApiKey apiKey) {
        mapper.save(apiKey, config);
        return apiKey.getKey();
    }

    public String deleteApiKeyTable() {

        Table table = dynamoDB.getTable(apiKeyTableName);
        try {
            table.delete();
            table.waitForDelete();
        } catch (Exception e) {
            logger.error("Error deleting api keys table, reason={}", e.getMessage());
        }
        return table.getTableName();
    }


    public void deleteApiKeyForRegistry(String registryName) {

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        scanExpression.addFilterCondition("Registry", new Condition().withComparisonOperator(ComparisonOperator.EQ)
            .withAttributeValueList(new AttributeValue().withS(registryName)));

        PaginatedScanList<ApiKey> apiKeys = mapper.scan(ApiKey.class, scanExpression, config);

        logger.info("Found {} API Keys", apiKeys.size());

        for (ApiKey apiKey : apiKeys) {
            logger.info("Deleting API Key {}", apiKey);
            mapper.delete(apiKey, config);
        }
    }

}
