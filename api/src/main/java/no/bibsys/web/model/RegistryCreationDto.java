package no.bibsys.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Class for creating the Rest-API documentation for creating a new registry. When we create a new registry we do not
 * expect a validation schema. The validation schema will be set after the registry has been created.
 */
@XmlRootElement
public class RegistryCreationDto {


    private String id;
    private String apiKey;
    private String path;
    private Map<String, Object> metadata;


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

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }


    @Override
    public String toString() {
        return "RegistryDto [id=" + id + ", apiKey=" + apiKey + ", path=" + path + ", metadata=" + metadata + "]";
    }

    @JsonIgnore
    public RegistryDto toRegistryDto() {
        RegistryDto registryDto = new RegistryDto();
        registryDto.setId(this.id);
        registryDto.setApiKey(this.apiKey);
        registryDto.setMetadata(this.metadata);
        registryDto.setPath(this.path);
        return registryDto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RegistryCreationDto)) {
            return false;
        }

        RegistryCreationDto that = (RegistryCreationDto) o;

        if (!getId().equals(that.getId())) {
            return false;
        }
        if (!getApiKey().equals(that.getApiKey())) {
            return false;
        }
        if (!getPath().equals(that.getPath())) {
            return false;
        }
        return getMetadata().equals(that.getMetadata());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getApiKey().hashCode();
        result = 31 * result + getPath().hashCode();
        result = 31 * result + getMetadata().hashCode();
        return result;
    }
}
