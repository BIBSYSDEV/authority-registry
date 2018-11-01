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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contributor == null) ? 0 : contributor.hashCode());
        result = prime * result + ((createDate == null) ? 0 : createDate.hashCode());
        result = prime * result + ((creator == null) ? 0 : creator.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((license == null) ? 0 : license.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((registryName == null) ? 0 : registryName.hashCode());
        result = prime * result + ((sameAs == null) ? 0 : sameAs.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Metadata other = (Metadata) obj;
        if (contributor == null) {
            if (other.contributor != null)
                return false;
        } else if (!contributor.equals(other.contributor))
            return false;
        if (createDate == null) {
            if (other.createDate != null)
                return false;
        } else if (!createDate.equals(other.createDate))
            return false;
        if (creator == null) {
            if (other.creator != null)
                return false;
        } else if (!creator.equals(other.creator))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (license == null) {
            if (other.license != null)
                return false;
        } else if (!license.equals(other.license))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (registryName == null) {
            if (other.registryName != null)
                return false;
        } else if (!registryName.equals(other.registryName))
            return false;
        if (sameAs == null) {
            if (other.sameAs != null)
                return false;
        } else if (!sameAs.equals(other.sameAs))
            return false;
        return true;
    }
    
}
