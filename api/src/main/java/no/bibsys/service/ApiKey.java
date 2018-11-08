package no.bibsys.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGenerateStrategy;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedTimestamp;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel.DynamoDBAttributeType;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTyped;

public class ApiKey {

    private String key;
    private List<String> roles;
    private boolean active;
    private Date modified;
    
    public ApiKey() {
        // TODO Auto-generated constructor stub
    }
    
    public ApiKey(String... roles) {
        this.roles = Arrays.asList(roles);
        this.active = true;
    }
    
    @DynamoDBHashKey(attributeName = "Key")
    @DynamoDBAutoGeneratedKey
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    @DynamoDBAttribute(attributeName = "Roles")
    public List<String> getRoles() {
        return roles;
    }
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    @DynamoDBAttribute(attributeName = "Active")
    @DynamoDBTyped(DynamoDBAttributeType.BOOL)
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    @DynamoDBAttribute(attributeName = "Modified")
    @DynamoDBAutoGeneratedTimestamp(strategy = DynamoDBAutoGenerateStrategy.ALWAYS)
    public Date getModified() {
        return modified;
    }
    public void setModified(Date modified) {
        this.modified = modified;
    }
    
    
    
}
