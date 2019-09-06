package no.bibsys.web.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegistryInfoUiSchemaDto {


    private String id;
    private String uischema;

    public RegistryInfoUiSchemaDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUischema() {
        return uischema;
    }

    public void setUischema(String uischema) {
        this.uischema = uischema;
    }
}

