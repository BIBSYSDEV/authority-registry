package no.bibsys.web.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement
public class QueryResultDto {

    private String id;
    private Map<String, Object> fields;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String,Object> getFields() {
        return fields;
    }

    public void setFields(Map<String,Object> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "RegistryDto [id=" + id + ", fields=" + fields + "]";
    }
}
