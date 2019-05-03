package no.bibsys.service;


import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import no.bibsys.EnvironmentVariables;
import no.bibsys.aws.tools.Environment;
import no.bibsys.db.RegistryManager;
import no.bibsys.db.exceptions.RegistryCreationFailureException;
import no.bibsys.db.exceptions.RegistryMetadataTableBeingCreatedException;
import no.bibsys.db.exceptions.RegistryNotFoundException;
import no.bibsys.db.exceptions.RegistryUnavailableException;
import no.bibsys.db.exceptions.SettingValidationSchemaUponCreationException;
import no.bibsys.db.structures.Registry;
import no.bibsys.db.structures.RegistryStatus;
import no.bibsys.entitydata.validation.exceptions.ShaclModelValidationException;
import no.bibsys.entitydata.validation.exceptions.TargetClassPropertyObjectIsNotAResourceException;
import no.bibsys.service.exceptions.UnknownStatusException;
import no.bibsys.web.exception.ApiKeyTableBeingCreatedException;
import no.bibsys.web.model.RegistryConverter;
import no.bibsys.web.model.RegistryDto;
import no.bibsys.web.model.RegistryInfoNoMetadataDto;

public class RegistryService {

    private static final String UNKNOWN_STATUS_FOR_REGISTRY = "Unknown Status for registry";
    private final transient RegistryManager registryManager;
    private final transient AuthenticationService authenticationService;
    private final transient String registryMetadataTableName;

    public RegistryService(RegistryManager registryManager, AuthenticationService authenticationService,
        Environment environmentReader) {
        this.registryManager = registryManager;
        this.authenticationService = authenticationService;

        registryMetadataTableName = environmentReader.readEnv(EnvironmentVariables.REGISTRY_METADATA_TABLE_NAME);
    }

    public RegistryInfoNoMetadataDto createRegistry(RegistryDto registryDto)
            throws RegistryMetadataTableBeingCreatedException, SettingValidationSchemaUponCreationException,
            RegistryCreationFailureException {

        Registry registry = registryManager
            .createRegistry(registryMetadataTableName, RegistryConverter.toRegistry(registryDto));

        ApiKey apiKey = ApiKey.createRegistryAdminApiKey(registry.getId());
        String savedApiKey = authenticationService.saveApiKey(apiKey);

        RegistryInfoNoMetadataDto registryInfoNoMetadataDto = new RegistryInfoNoMetadataDto(registryDto);
        registryInfoNoMetadataDto.setPath("/registry/" + registryInfoNoMetadataDto.getId());
        registryInfoNoMetadataDto.setApiKey(savedApiKey);

        return registryInfoNoMetadataDto;
    }

    public RegistryDto getRegistry(String registryId) {
        Registry registry = registryManager.getRegistry(registryMetadataTableName, registryId);
        return RegistryConverter.toRegistryDto(registry);
    }

    public RegistryInfoNoMetadataDto getRegistryInfo(String registryName)  {

        RegistryDto registry = getRegistry(registryName);

        return new RegistryInfoNoMetadataDto(registry);
    }

    public void deleteRegistry(String registryId) {
        registryManager.deleteRegistry(registryMetadataTableName, registryId);
        authenticationService.deleteApiKeyForRegistry(registryId);
    }

    public List<String> getRegistries() {
        return registryManager.getRegistries(registryMetadataTableName);
    }

    public RegistryDto updateRegistrySchema(String registryId, String schema)
        throws IOException, ShaclModelValidationException, TargetClassPropertyObjectIsNotAResourceException {
        Registry registry = registryManager.updateRegistrySchema(registryMetadataTableName, registryId, schema);
        return RegistryConverter.toRegistryDto(registry);
    }

    public RegistryDto updateRegistryMetadata(RegistryDto registryDto) {
        Registry registry = registryManager
            .updateRegistryMetadata(registryMetadataTableName, RegistryConverter.toRegistry(registryDto));
        return RegistryConverter.toRegistryDto(registry);
    }

    public RegistryStatus validateRegistryExists(String registryName) throws UnknownStatusException {
        
        RegistryStatus metaDataTableStatus = registryManager.status(registryMetadataTableName);
        switch (metaDataTableStatus) {
            case ACTIVE:
                break;
            case CREATING:
            case UPDATING:
                throw new RegistryUnavailableException(registryName, metaDataTableStatus.name().toLowerCase(Locale.ENGLISH));
            case DELETING:
            case NOT_FOUND:
                throw new RegistryNotFoundException(registryName);
            default:
                throw new UnknownStatusException(UNKNOWN_STATUS_FOR_REGISTRY);
        }
        
        RegistryStatus status = registryManager.status(registryName);
        switch (status) {
            case ACTIVE:
                return status;
            case CREATING:
            case UPDATING:
                throw new RegistryUnavailableException(registryName, status.name().toLowerCase(Locale.ENGLISH));
            case DELETING:
            case NOT_FOUND:
                throw new RegistryNotFoundException(registryName);
            default:
                throw new UnknownStatusException(UNKNOWN_STATUS_FOR_REGISTRY);
        }
    }

    public String replaceApiKey(String registryName, String oldApiKey) throws ApiKeyTableBeingCreatedException {

        ApiKey existingApiKey = authenticationService.getApiKey(oldApiKey);
        if (Objects.isNull(existingApiKey.getRegistry()) || !existingApiKey.getRegistry().equals(registryName)) {
            throw new IllegalArgumentException(String.format("Wrong apikey supplied for registry %s", registryName));
        }

        Registry registry = registryManager.getRegistry(registryMetadataTableName, registryName);
        ApiKey apiKey = ApiKey.createRegistryAdminApiKey(registry.getId());
        authenticationService.deleteApiKeyForRegistry(registryName);
        return authenticationService.saveApiKey(apiKey);
    }
}
