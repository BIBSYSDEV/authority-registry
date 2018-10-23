package no.bibsys.web.model;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EditRegistryRequest {

    public static final String CREATE = "create";
    public static final String EMPTY = "empty";


    private String action;
    private String registryName;
    private String[] label;
    private String license;
    private String[] contributor;
    private String[] creator;
    private String description;
    private String[] sameAs;

    public EditRegistryRequest() {}


    public EditRegistryRequest(String action) {
        this.action = action;
    }


    public EditRegistryRequest(String action, String registryName) {
        this(action);
        this.registryName = registryName;
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EditRegistryRequest)) {
            return false;
        }

        EditRegistryRequest that = (EditRegistryRequest) o;

        if (action != null ? !action.equals(that.action) : that.action != null) {
            return false;
        }
        return registryName != null ? registryName.equals(that.registryName)
                : that.registryName == null;
    }

    @Override
    public final int hashCode() {
        int result = action != null ? action.hashCode() : 0;
        result = 31 * result + (registryName != null ? registryName.hashCode() : 0);
        return result;
    }


    public final String getAction() {
        return action;
    }


    // TODO should PMD rule "Bean member should serialize" be disabled or not?
    public final void setAction(String action) {
        this.action = action;
    }

    public final String getRegistryName() {
        return registryName;
    }


    public final void setRegistryName(String registryName) {
        this.registryName = registryName;
    }


    public final EditRegistryRequest specify() {
        switch (action) {
        case CREATE:
            return new CreateRegistryRequest(registryName);
        case EMPTY:
            return new EmptyRegistryRequest(registryName);
        default:
            throw new IllegalStateException("Invalid action");
        }
    }

    public Map<String, Object> createAttributeMap() {

        Map<String, Object> attributeMap = new ConcurrentHashMap<>();
        if(label != null) {
            attributeMap.put("label", label);
        }
        if(license != null) {
            attributeMap.put("license", license);
        }
        if(contributor != null) {
            attributeMap.put("contributor", contributor);
        }
        if(creator != null) {
            attributeMap.put("creator", creator);
        }
        if(description != null) {
            attributeMap.put("description", description);
        }
        if(sameAs != null) {
            attributeMap.put("sameAs", sameAs);
        }

        return attributeMap;
    }


    public String[] getLabel() {
        if(label == null) {
            return new String[0];
        }else {
            return Arrays.copyOf(label, label.length);
        }
    }


    public void setLabel(String... label) {
        this.label = Arrays.copyOf(label, label.length);
    }


    public String getLicense() {
        return license;
    }


    public void setLicense(String license) {
        this.license = license;
    }


    public String[] getContributor() {
        if(contributor == null) {
            return new String[0];
        }else {
            return Arrays.copyOf(contributor, contributor.length);
        }
    }


    public void setContributor(String... contributor) {
        this.contributor = Arrays.copyOf(contributor, contributor.length);
    }


    public String[] getCreator() {
        if(creator == null) {
            return new String[0];
        }else {
            return Arrays.copyOf(creator, creator.length);
        }
    }


    public void setCreator(String... creator) {
        this.creator = Arrays.copyOf(creator, creator.length);
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String[] getSameAs() {
        if(sameAs == null) {
            return new String[0];
        }else {
            return Arrays.copyOf(sameAs, sameAs.length);
        }
    }


    public void setSameAs(String... sameAs) {
        this.sameAs = Arrays.copyOf(sameAs, sameAs.length);
    }

}
