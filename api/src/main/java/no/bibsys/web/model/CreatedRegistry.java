package no.bibsys.web.model;

public class CreatedRegistry {

	private String message;
    private String registryName;
	private String apiKey;
	private String status;
	
	public CreatedRegistry() {}
	
	public CreatedRegistry(String message) {
		this(message, null, null, null);
	}
	
	public CreatedRegistry(String message, String registryName) {
		this(message, registryName, null, null);
	}
	
	public CreatedRegistry(String message, String registryName, String apiKey, String status) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
	
	
}