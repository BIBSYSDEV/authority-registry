package no.bibsys.service;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.SSESpecification;
import no.bibsys.EnvironmentReader;
import no.bibsys.web.security.ApiKeyConstants;
import no.bibsys.web.security.Roles;

public class AuthenticationService {
    
    private final static String TEST_STAGE_NAME = "test";
    
    private final transient DynamoDBMapper mapper;
    private final transient DynamoDBMapperConfig config;
    private final transient EnvironmentReader environmentReader;
    private final transient String apiKeyTableName;
    private final transient DynamoDB dynamoDB;
    private final static Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    
    public AuthenticationService(AmazonDynamoDB client, EnvironmentReader environmentReader) {
        this.environmentReader = environmentReader;
        mapper = new DynamoDBMapper(client);
        this.dynamoDB = new DynamoDB(client);
        
        apiKeyTableName = environmentReader.getEnvForName(ApiKeyConstants.API_KEY_TABLE_NAME).orElse("entity-registry-api-keys");
        
        config = DynamoDBMapperConfig
                .builder()
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(apiKeyTableName))
                .build();
    }
    
    public String createApiKey(String... roles) {
        ApiKey apiKey = new ApiKey(roles);
        mapper.save(apiKey, config);
        return apiKey.getKey();
    }

    public ApiKey getApiKey(String apiKeyInHeader) {
        return mapper.load(ApiKey.class, apiKeyInHeader, config);
    }

    public String getApiKeyTableName() {
        return apiKeyTableName;
    }

    public String createApiKeyTable() {
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
    
    public void setUpInitialApiKeys() {
        if (environmentReader.getStageName().equals(TEST_STAGE_NAME)) {
            ApiKey apiAdminApiKey = new ApiKey(Roles.API_ADMIN);
            apiAdminApiKey.setKey("testApiAdminApiKey");
            mapper.save(apiAdminApiKey, config);
            ApiKey registryAdminapiKey = new ApiKey(Roles.REGISTRY_ADMIN);
            registryAdminapiKey.setKey("testRegistryAdminApiKey");
            mapper.save(registryAdminapiKey, config);
        } else {
            createApiKey(Roles.API_ADMIN);
            createApiKey(Roles.REGISTRY_ADMIN);
        }
    }

    public String deleteApiKeyTable() {
        
        Table table = dynamoDB.getTable(apiKeyTableName);
        try {
            table.delete();
            table.waitForDelete();
        } catch (Exception e) {
            logger.error("Error deleting api keys table", e);
        }
        return table.getTableName();
    }
    
    public void saveApiKey(ApiKey apiKey) {
        mapper.save(apiKey, config);
    }
    
}
