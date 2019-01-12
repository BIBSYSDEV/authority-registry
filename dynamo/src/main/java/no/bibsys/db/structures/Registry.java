package no.bibsys.db.structures;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Objects;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedJson;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Registry {

    private String id;
    private ObjectNode metadata;
    private String schema;
    
    @DynamoDBHashKey(attributeName = "id")
    @DynamoDBAutoGeneratedKey
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    @DynamoDBAttribute(attributeName = "metadata")
    @DynamoDBTypeConvertedJson()
    public ObjectNode getMetadata() {
        return metadata;
    }
    
    public void setMetadata(ObjectNode metadata) {
        this.metadata = metadata;
    }
    
    @DynamoDBAttribute(attributeName = "schema")
    public String getSchema() {
        return schema;
    }
    
    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Override
    @DynamoDBIgnore
    public int hashCode() {
        return Objects.hash(id, metadata, schema);
    }

    @Override
    @DynamoDBIgnore
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Registry)) {
            return false;
        }
        Registry other = (Registry) obj;
        return Objects.equals(id, other.id) && Objects.equals(metadata, other.metadata)
                && Objects.equals(schema, other.schema);
    }


    
    
}
