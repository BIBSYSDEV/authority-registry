package no.bibsys.web.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import javax.xml.bind.DatatypeConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class EntityDto {

    private String id;
    private String created;
    private String modified;
    private String path;
    private String body;

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
    
    public String getBody() {
        return body;
    }

    @JsonIgnore
    public String getBodyAsJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(body);
    }

    public void setBody(String body) {
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
        return Objects.hash(body);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof EntityDto)) {
            return false;
        }
        EntityDto other = (EntityDto) obj;
        return Objects.equals(body, other.body);
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
