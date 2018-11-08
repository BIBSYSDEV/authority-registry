package no.bibsys.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;

public class TestApiKey extends ApiKey {

    public TestApiKey(String key, String roles) {
        super(roles);
        setKey(key);
    }

    @Override
    @DynamoDBHashKey(attributeName = "Key")
    public String getKey() {
        return super.getKey();
    }
    
}
