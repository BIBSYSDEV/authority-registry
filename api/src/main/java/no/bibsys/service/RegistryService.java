package no.bibsys.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import no.bibsys.EnvironmentVariables;
import no.bibsys.aws.tools.Environment;
import no.bibsys.db.RegistryManager;
import no.bibsys.db.RegistryManager.RegistryStatus;
import no.bibsys.db.exceptions.RegistryMetadataTableBeingCreatedException;
import no.bibsys.db.exceptions.RegistryNotFoundException;
import no.bibsys.db.exceptions.RegistryUnavailableException;
import no.bibsys.db.exceptions.SettingValidationSchemaUponCreationException;
import no.bibsys.db.structures.Registry;
import no.bibsys.entitydata.validation.exceptions.ShaclModelValidationException;
import no.bibsys.service.exceptions.UnknownStatusException;
import no.bibsys.web.model.RegistryConverter;
import no.bibsys.web.model.RegistryDto;
import no.bibsys.web.model.RegistryInfoDto;

public class RegistryService {

    public static final String UNKNOWN_STATUS_FOR_REGISTRY = "Unknown Status for registry";
    private final transient RegistryManager registryManager;
    private final transient AuthenticationService authenticationService;
    private final transient String registryMetadataTableName;

    public RegistryService(RegistryManager registryManager, AuthenticationService authenticationService,
        Environment environmentReader) {
        this.registryManager = registryManager;
        this.authenticationService = authenticationService;

        registryMetadataTableName = environmentReader.readEnv(EnvironmentVariables.REGISTRY_METADATA_TABLE_NAME);
    }

    public RegistryInfoDto createRegistry(RegistryDto registryDto)
        throws RegistryMetadataTableBeingCreatedException, SettingValidationSchemaUponCreationException {

        Registry registry = registryManager
            .createRegistry(registryMetadataTableName, RegistryConverter.toRegistry(registryDto));

        ApiKey apiKey = ApiKey.createRegistryAdminApiKey(registry.getId());
        String savedApiKey = authenticationService.saveApiKey(apiKey);

        RegistryInfoDto registryInfoDto = new RegistryInfoDto(registryDto);
        registryInfoDto.setPath("/registry/" + registryInfoDto.getId());
        registryInfoDto.setApiKey(savedApiKey);

        return registryInfoDto;
    }

    public RegistryDto getRegistry(String registryId) {
        Registry registry = registryManager.getRegistry(registryMetadataTableName, registryId);
        return RegistryConverter.toRegistryDto(registry);
    }

    public RegistryInfoDto getRegistryInfo(String registryName) throws JsonProcessingException {

        RegistryDto registry = getRegistry(registryName);

        return new RegistryInfoDto(registry);
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

    public RegistryDto updateRegistryMetadata(RegistryDto registryDto) throws IOException {
        Registry registry = registryManager
            .updateRegistryMetadata(registryMetadataTableName, RegistryConverter.toRegistry(registryDto));
        return RegistryConverter.toRegistryDto(registry);
    }

    public void validateRegistryExists(String registryName) throws UnknownStatusException {
        RegistryStatus status = registryManager.status(registryName);
        switch (status) {
            case ACTIVE:
                return;
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

    public String replaceApiKey(String registryName, String oldApiKey) {

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
