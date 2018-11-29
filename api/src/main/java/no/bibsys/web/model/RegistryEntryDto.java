package no.bibsys.web.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class RegistryEntryDto {

    private String id;
    private ObjectNode metadata;
    private String schema;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public ObjectNode getMetadata() {
        return metadata;
    }
    
    public void setMetadata(ObjectNode metadata) {
        this.metadata = metadata;
    }
    
    public String getSchema() {
        return schema;
    }
    
    public void setSchema(String schema) {
        this.schema = schema;
    }
    
    
    
}
