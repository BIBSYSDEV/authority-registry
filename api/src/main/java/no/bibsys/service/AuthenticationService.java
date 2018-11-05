package no.bibsys.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;

public class AuthenticationService {
    
    private final transient DynamoDBMapper mapper;
    private final transient DynamoDBMapperConfig config;
    
    public AuthenticationService(AmazonDynamoDB client, String tableName) {
        mapper = new DynamoDBMapper(client);
        config = DynamoDBMapperConfig
                .builder()
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(tableName))
                .build();
    }
    
    public ApiKey createApiKey(String... roles) {
        ApiKey apiKey = new ApiKey(roles);
        mapper.save(apiKey, config);
        return apiKey;
    }
    
}
