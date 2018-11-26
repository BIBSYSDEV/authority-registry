package no.bibsys.web.model;

public class CreatedRegistry {

    private String message;
    private String registryName;
    private String apiKey;

    public CreatedRegistry() {}

    public CreatedRegistry(String message) {
        this(message, null, null);
    }

    public CreatedRegistry(String message, String registryName) {
        this(message, registryName, null);
    }

    public CreatedRegistry(String message, String registryName, String apiKey) {
        this.message = message;
        this.registryName = registryName;
        this.apiKey = apiKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRegistryName() {
        return registryName;
    }

    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }


}
