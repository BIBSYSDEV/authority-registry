package no.bibsys.web.model;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class RegistryDto {

    private String id;
    private String apiKey;
    private String path;
    private Map<String, Object> metadata;
    private String schema;
    private String uiSchema;

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

    public Map<String,Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String,Object> metadata) {
        this.metadata = metadata;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
    
    public void setUiSchema(String uiSchema) {
        this.uiSchema = uiSchema;
    }

    public String getUiSchema() {
        return uiSchema;
    }

    @Override
    public String toString() {
        return "RegistryDto [id=" + id + ", apiKey=" + apiKey + ", path=" + path + ", metadata="
            + metadata
            + ", schema=" + schema + "]";
    }
}
