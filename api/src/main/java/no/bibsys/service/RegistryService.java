package no.bibsys.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import no.bibsys.db.RegistryManager;
import no.bibsys.db.structures.EntityRegistryTemplate;
import no.bibsys.web.exception.RegistryAlreadyExistsException;
import no.bibsys.web.model.CreatedRegistry;

public class RegistryService {

	private final transient AuthenticationService authenticationService;
	private final transient RegistryManager registryManager;
	
	public RegistryService(AuthenticationService authenticationService, RegistryManager registryManager) {
		this.authenticationService = authenticationService;
		this.registryManager = registryManager;
	}
	
	public CreatedRegistry createRegistry(EntityRegistryTemplate template) throws JsonProcessingException {
		String registryName = template.getId();
		
		if (registryManager.registryExists(registryName)) {
			throw new RegistryAlreadyExistsException(registryName);
		} else {
			boolean registryCreated = registryManager.createRegistryFromTemplate(template);
			
			if (registryCreated) {
				
				ApiKey apiKey = ApiKey.createRegistryAdminApiKey(registryName);
				String savedApiKey = authenticationService.saveApiKey(apiKey);
				
				return new CreatedRegistry(String.format("A registry with name=%s is being created", registryName), registryName, savedApiKey, String.format("/registry/%s/status", registryName));
				
			}
				
			return new CreatedRegistry("Registry NOT created. See log for details");
		}	
	}
	
	public void deleteRegistry(String registryName) {
		boolean registryDeleted = registryManager.deleteRegistry(registryName);
		
		if (registryDeleted) {
			authenticationService.deleteApiKeyForRegistry(registryName);
		}
	}
	
}