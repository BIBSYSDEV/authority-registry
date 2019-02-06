package no.bibsys.web.model;


public class RegistryInfoNoMetadataDto {

    private String id;
    private String apiKey;
    private String path;
    private String schema;

    public RegistryInfoNoMetadataDto() {
    }
    
    public RegistryInfoNoMetadataDto(RegistryDto registry) {
        id = registry.getId();
        apiKey = registry.getApiKey();
        path = registry.getPath();
        schema = registry.getSchema();
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

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
}
