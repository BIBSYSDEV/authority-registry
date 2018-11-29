package no.bibsys.web.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class EntityDto {

    private String id;
    private String created;
    private String modified;
    private String path;
    private ObjectNode body;

    public EntityDto() {}

    public EntityDto(String json) {
        try {
            this.body = new ObjectMapper().readValue(json, ObjectNode.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Could not parse Json into Entity body: " + e.getMessage());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public ObjectNode getBody() {
        return body;
    }

    @JsonIgnore
    public String getBodyAsJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(body);
    }

    public void setBody(ObjectNode body) {
        this.body = body;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    @JsonIgnore
    public String getEtagValue() {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(getBodyAsJson().getBytes(StandardCharsets.UTF_8));
            String hex = DatatypeConverter.printHexBinary(hash);
            return hex;            
        } catch (NoSuchAlgorithmException | JsonProcessingException e) {
            return null;
        }

    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + ((body == null) ? 0 : body.hashCode());
        return result;
    }

    @Override
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
        EntityDto other = (EntityDto) obj;
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
    public String toString() {
        return "EntityDto [id=" + id + ", created=" + created + ", modified=" + modified + ", body="
                + body + "]";
    }

    public void validate() {
        // TODO: validate body
    }
    
}
