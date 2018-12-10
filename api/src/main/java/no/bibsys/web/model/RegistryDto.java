package no.bibsys.web.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class RegistryDto {

    private String id;
    private String apiKey;
    private String path;
    private String metadata;
    private String schema;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    @JsonRawValue
    @JsonDeserialize(using = JsonAsStringDeserializer.class)
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    public String getSchema() {
        return schema;
    }
    
    public void setSchema(String schema) {
        this.schema = schema;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, metadata, schema);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RegistryDto)) {
            return false;
        }
        RegistryDto other = (RegistryDto) obj;
        return Objects.equals(id, other.id) && Objects.equals(metadata, other.metadata)
                && Objects.equals(schema, other.schema);
    }


    
    
    
}
