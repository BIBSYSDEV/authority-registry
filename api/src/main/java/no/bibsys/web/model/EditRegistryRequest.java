package no.bibsys.web.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
        return registryName == null?"":registryName;
    }


    public final void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    public Map<String, Object> createAttributeMap() {

        Map<String, Object> attributeMap = new ConcurrentHashMap<>();
        attributeMap.put("label", getLabel());
        attributeMap.put("license", getLicense());
        attributeMap.put("contributor", getContributor());
        attributeMap.put("creator", getCreator());
        attributeMap.put("description", getDescription());
        attributeMap.put("sameAs", getSameAs());

        // attributeMap can't contain null, "" or empty lists
        Map<String, Object> newAttributeMap = attributeMap.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .filter(entry -> entry.getValue() instanceof String && !((String)entry.getValue()).isEmpty() || entry.getValue() instanceof List && !((List<?>)entry.getValue()).isEmpty())
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
        
        return newAttributeMap;
    }


    public List<String> getLabel() {
        if(label == null) {
            return new ArrayList<String>();
        }else {
            return new ArrayList<String>(label);
        }
    }


    public void setLabel(List<String> label) {
        this.label = new ArrayList<String>(label);
    }


    public String getLicense() {
        return license == null?"":license;
    }


    public void setLicense(String license) {
        this.license = license;
    }


    public List<String> getContributor() {
        if(contributor == null) {
            return new ArrayList<String>();
        }else {
            return new ArrayList<String>(contributor);
        }
    }


    public void setContributor(List<String> contributor) {
        this.contributor = new ArrayList<String>(contributor);
    }


    public List<String> getCreator() {
        if(creator == null) {
            return new ArrayList<String>();
        }else {
            return new ArrayList<String>(creator);
        }
    }


    public void setCreator(List<String> creator) {
        this.creator = new ArrayList<String>(creator);
    }


    public String getDescription() {
        return description == null?"":description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public List<String> getSameAs() {
        if(sameAs == null) {
            return new ArrayList<String>();
        }else {
            return new ArrayList<String>(sameAs);
        }
    }


    public void setSameAs(List<String> sameAs) {
        this.sameAs = new ArrayList<String>(sameAs);
    }
}
