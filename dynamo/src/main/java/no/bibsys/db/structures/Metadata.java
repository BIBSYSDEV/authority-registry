package no.bibsys.db.structures;

import java.util.Date;
import java.util.List;

public class Metadata {

    private String name;
    private Date createDate;
    private String registryName;
    private List<String> label;
    private String license;
    private List<String> contributor;
    private List<String> creator;
    private String description;
    private List<String> sameAs;

    public Metadata() {
        createDate = new Date();
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getRegistryName() {
        return registryName;
    }

    public void setRegistryName(String registryName) {
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

    @Override
    public String toString() {
        return "Metadata [name=" + name + ", createDate=" + createDate + ", registryName=" + registryName + ", label="
                + label + ", license=" + license + ", contributor=" + contributor + ", creator=" + creator
                + ", description=" + description + ", sameAs=" + sameAs + "]";
    }
}
