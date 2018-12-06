package no.bibsys.service;

import java.util.List;
import java.util.Locale;
import javax.ws.rs.core.Response.Status;
import no.bibsys.EnvironmentReader;
import no.bibsys.db.RegistryManager;
import no.bibsys.db.RegistryManager.RegistryStatus;
import no.bibsys.db.exceptions.RegistryNotFoundException;
import no.bibsys.db.exceptions.RegistryUnavailableException;
import no.bibsys.db.structures.Registry;
import no.bibsys.web.model.RegistryConverter;
import no.bibsys.web.model.RegistryDto;

public class RegistryService {

    private final transient RegistryManager registryManager;
    private final transient AuthenticationService authenticationService;
    private final transient String validationSchemaTableName;

    
    public RegistryService(RegistryManager registryManager, 
            AuthenticationService authenticationService, 
            EnvironmentReader environmentReader) {
        this.registryManager = registryManager;
        this.authenticationService = authenticationService;
        
        validationSchemaTableName = environmentReader.getEnvForName(EnvironmentReader.VALIDATION_SCHEMA_TABLE_NAME);
    }
    
    public RegistryDto createRegistry(RegistryDto registryDto) {
        
        Registry registry = registryManager.createRegistry(
                validationSchemaTableName, RegistryConverter.toRegistry(registryDto)
                );
        
        ApiKey apiKey = ApiKey.createRegistryAdminApiKey(registry.getId());
        String savedApiKey = authenticationService.saveApiKey(apiKey);

        RegistryDto returnRegistryDto = RegistryConverter.toRegistryDto(registry);
        returnRegistryDto.setApiKey(savedApiKey);
        
        return returnRegistryDto;
    }
    
    public RegistryDto getRegistry(String registryId) {
        Registry registry = registryManager.getRegistry(validationSchemaTableName, registryId);
        return RegistryConverter.toRegistryDto(registry);
    }
    
    public void deleteRegistry(String registryId) {
        registryManager.deleteRegistry(validationSchemaTableName, registryId);
        authenticationService.deleteApiKeyForRegistry(registryId);
    }

    public List<String> getRegistries() {
        return registryManager.getRegistries(validationSchemaTableName);
    }

    public RegistryDto updateRegistry(RegistryDto registryDto) {
        Registry registry = registryManager.updateRegistry(validationSchemaTableName, RegistryConverter.toRegistry(registryDto));
        return RegistryConverter.toRegistryDto(registry);
    }

    public void emptyRegistry(String registryId) {
        registryManager.emptyRegistry(registryId);
        
    }

    public Status validateRegistryExists(String registryName) {
        RegistryStatus status = registryManager.status(registryName);
        switch(status) {
        case ACTIVE:
            return Status.CREATED;
        case CREATING:
        case UPDATING:
            throw new RegistryUnavailableException(registryName, status.name().toLowerCase(Locale.ENGLISH));
        case DELETING:
        case NOT_FOUND:
        default:
            throw new RegistryNotFoundException(registryName);
        }
}
    
}
