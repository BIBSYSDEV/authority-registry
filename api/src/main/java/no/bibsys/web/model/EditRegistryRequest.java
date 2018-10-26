package no.bibsys.web.model;

import java.util.List;
import java.util.Optional;
import no.bibsys.db.structures.Metadata;

public class EditRegistryRequest {

    private String registryName;
    private List<String> label;
    private String license;
    private List<String> contributor;
    private List<String> creator;
    private String description;
    private List<String> sameAs;

    public EditRegistryRequest() {}


    public EditRegistryRequest(String registryName) {
        this.registryName = registryName;
    }

    public final String getRegistryName() {
        return registryName;
    }


    public final void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    public List<String> getLabel() {
        return label;
    }


    public void setLabel(List<String> label) {
        this.label = label;
    }


    public String getLicense() {
        return license;
    }


    public void setLicense(String license) {
        this.license = license;
    }


    public List<String> getContributor() {
        return contributor;
    }


    public void setContributor(List<String> contributor) {
        this.contributor = contributor;
    }


    public List<String> getCreator() {
        return creator;
    }


    public void setCreator(List<String> creator) {
        this.creator = creator;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public List<String> getSameAs() {
        return sameAs;
    }


    public void setSameAs(List<String> sameAs) {
        this.sameAs = sameAs;
    }
    
    public void parseEditRegistryRequest(Metadata metadata) {
        metadata.setRegistryName(Optional.ofNullable(getRegistryName()).orElse(null));
        metadata.setLabel(Optional.ofNullable(getLabel()).orElse(null));
        metadata.setLicense(Optional.ofNullable(getLicense()).orElse(null));
        metadata.setContributor(Optional.ofNullable(getContributor()).orElse(null));
        metadata.setCreator(Optional.ofNullable(getCreator()).orElse(null));
        metadata.setDescription(Optional.ofNullable(getDescription()).orElse(null));
        metadata.setSameAs(Optional.ofNullable(getSameAs()).orElse(null));
    }
}
