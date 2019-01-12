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

//    @JsonRawValue
//    @JsonDeserialize(using = JsonAsStringDeserializer.class)
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

    @Override
    public String toString() {
        return "RegistryDto [id=" + id + ", apiKey=" + apiKey + ", path=" + path + ", metadata="
            + metadata
            + ", schema=" + schema + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RegistryDto)) {
            return false;
        }

        RegistryDto that = (RegistryDto) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) {
            return false;
        }
        if (getApiKey() != null ? !getApiKey().equals(that.getApiKey())
            : that.getApiKey() != null) {
            return false;
        }
        if (getPath() != null ? !getPath().equals(that.getPath()) : that.getPath() != null) {
            return false;
        }
        if (getMetadata() != null ? !getMetadata().equals(that.getMetadata())
            : that.getMetadata() != null) {
            return false;
        }
        return getSchema() != null ? getSchema().equals(that.getSchema())
            : that.getSchema() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getApiKey() != null ? getApiKey().hashCode() : 0);
        result = 31 * result + (getPath() != null ? getPath().hashCode() : 0);
        result = 31 * result + (getMetadata() != null ? getMetadata().hashCode() : 0);
        result = 31 * result + (getSchema() != null ? getSchema().hashCode() : 0);
        return result;
    }
}
