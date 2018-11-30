package no.bibsys.db.structures;

import java.util.Objects;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGenerateStrategy;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedTimestamp;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedJson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Entity {

    private String id;
    private String created;
    private String modified;
    private ObjectNode body;

    @DynamoDBHashKey(attributeName = "id")
    @DynamoDBAutoGeneratedKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBAttribute(attributeName = "created")
    @DynamoDBAutoGeneratedTimestamp(strategy = DynamoDBAutoGenerateStrategy.CREATE)
    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @DynamoDBAttribute(attributeName = "modified")
    @DynamoDBAutoGeneratedTimestamp(strategy = DynamoDBAutoGenerateStrategy.ALWAYS)
    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    @DynamoDBAttribute(attributeName = "body")
    @DynamoDBTypeConvertedJson()
    public ObjectNode getBody() {
        return body;
    }

    @DynamoDBIgnore
    public String getBodyAsJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(body);
    }

    public void setBody(ObjectNode body) {
        this.body = body;
    }

    @Override
    @DynamoDBIgnore
    public int hashCode() {
        return Objects.hash(body);
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
        if (!(obj instanceof Entity)) {
            return false;
        }
        Entity other = (Entity) obj;
        return Objects.equals(body, other.body);
    }

    @Override
    @DynamoDBIgnore
    public String toString() {
        return "Entity [id=" + id + ", created=" + created + ", modified=" + modified + ", body="
                + body + "]";
    }

}
