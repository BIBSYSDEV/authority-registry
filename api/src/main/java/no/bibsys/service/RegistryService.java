package no.bibsys.service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.ws.rs.core.Response.Status;
import no.bibsys.EnvironmentVariables;
import no.bibsys.aws.tools.Environment;
import no.bibsys.db.RegistryManager;
import no.bibsys.db.RegistryManager.RegistryStatus;
import no.bibsys.db.exceptions.RegistryMetadataTableBeingCreatedException;
import no.bibsys.db.exceptions.RegistryNotFoundException;
import no.bibsys.db.exceptions.RegistryUnavailableException;
import no.bibsys.db.structures.Registry;
import no.bibsys.entitydata.validation.exceptions.ShaclModelValidationException;
import no.bibsys.web.model.RegistryConverter;
import no.bibsys.web.model.RegistryDto;

public class RegistryService {

    private final transient RegistryManager registryManager;
    private final transient AuthenticationService authenticationService;
    private final transient String registryMetadataTableName;


    public RegistryService(RegistryManager registryManager, AuthenticationService authenticationService,
        Environment environmentReader) {
        this.registryManager = registryManager;
        this.authenticationService = authenticationService;

        registryMetadataTableName = environmentReader.readEnv(EnvironmentVariables.REGISTRY_METADATA_TABLE_NAME);
    }

    public RegistryDto createRegistry(RegistryDto registryDto)
        throws RegistryMetadataTableBeingCreatedException, IOException, ShaclModelValidationException {

        Registry registry = registryManager
            .createRegistry(registryMetadataTableName, RegistryConverter.toRegistry(registryDto));

        ApiKey apiKey = ApiKey.createRegistryAdminApiKey(registry.getId());
        String savedApiKey = authenticationService.saveApiKey(apiKey);

        RegistryDto returnRegistryDto = RegistryConverter.toRegistryDto(registry);
        returnRegistryDto.setApiKey(savedApiKey);

        return returnRegistryDto;
    }

    public RegistryDto getRegistry(String registryId) {
        Registry registry = registryManager.getRegistry(registryMetadataTableName, registryId);
        return RegistryConverter.toRegistryDto(registry);
    }

    public void deleteRegistry(String registryId) {
        registryManager.deleteRegistry(registryMetadataTableName, registryId);
        authenticationService.deleteApiKeyForRegistry(registryId);
    }

    public List<String> getRegistries() {
        return registryManager.getRegistries(registryMetadataTableName);
    }

    public RegistryDto updateRegistrySchema(String registryId, String schema)
        throws IOException, ShaclModelValidationException {
        Registry registry = registryManager.updateRegistrySchema(registryMetadataTableName, registryId, schema);
        return RegistryConverter.toRegistryDto(registry);
    }

    public RegistryDto updateRegistryMetadata(RegistryDto registryDto) {
        Registry registry = registryManager
            .updateRegistryMetadata(registryMetadataTableName, RegistryConverter.toRegistry(registryDto));
        return RegistryConverter.toRegistryDto(registry);
    }

    public void emptyRegistry(String registryId) {
        registryManager.emptyRegistry(registryId);

    }

    public Status validateRegistryExists(String registryName) {
        RegistryStatus status = registryManager.status(registryName);
        switch (status) {
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

    public String replaceApiKey(String registryName, String oldApiKey) {

        ApiKey existingApiKey = authenticationService.getApiKey(oldApiKey);
        if (Objects.isNull(existingApiKey.getRegistry()) || !existingApiKey.getRegistry().equals(registryName)) {
            throw new IllegalArgumentException(String.format("Wrong apikey supplied for registry %s", registryName));
        }

        Registry registry = registryManager.getRegistry(registryMetadataTableName, registryName);
        ApiKey apiKey = ApiKey.createRegistryAdminApiKey(registry.getId());
        authenticationService.deleteApiKeyForRegistry(registryName);
        String savedApiKey = authenticationService.saveApiKey(apiKey);

        return savedApiKey;
    }

}
