package no.bibsys.db;

import java.io.IOException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGenerateStrategy;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedTimestamp;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedJson;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Entity {

    private String id;
    private String created;
    private String modified;
    private String path;
    private ObjectNode body;

    public Entity() {}

    @SuppressWarnings("unchecked")
    public Entity(String json) {
        try {
            this.body = new ObjectMapper().readValue(json, ObjectNode.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Could not parse Json into Entity body: " + e.getMessage());
        }
    }

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
    @JsonIgnore
    public String getBodyAsJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(body);
    }

    public void setBody(ObjectNode body) {
        this.body = body;
    }

    @DynamoDBIgnore
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    @DynamoDBIgnore
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + ((body == null) ? 0 : body.hashCode());
        return result;
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        Entity other = (Entity) obj;
        if (body == null) {
            if (other.body != null) {
                return false;
            }
        } else if (!body.equals(other.body)) {
            return false;
        }
        return true;
    }

    @Override
    @DynamoDBIgnore
    public String toString() {
        return "Entity [id=" + id + ", created=" + created + ", modified=" + modified + ", body="
                + body + "]";
    }

    @DynamoDBIgnore
    public void validate() {
        // TODO: validate body
    }


}
