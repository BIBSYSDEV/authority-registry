package no.bibsys.service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.bibsys.EnvironmentReader;
import no.bibsys.db.ItemDriver;
import no.bibsys.db.JsonUtils;
import no.bibsys.db.TableDriver;
import no.bibsys.web.exception.RegistryAlreadyExistsException;
import no.bibsys.web.exception.RegistryNotFoundException;
import no.bibsys.web.exception.RegistryUnavailableException;
import no.bibsys.web.model.CreatedRegistryDto;
import no.bibsys.web.model.RegistryEntryDto;

public class RegistryManager {

    public enum RegistryStatus {
        CREATING, UPDATING, DELETING, ACTIVE, NOT_FOUND;
    }

    private final transient TableDriver tableDriver;
    private final transient ItemDriver itemDriver;
    private final transient AuthenticationService authenticationService;
    private final transient String validationSchemaTableName;
    private final transient ObjectMapper objectMapper = JsonUtils.getObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(RegistryManager.class);

    public RegistryManager(TableDriver tableManager, ItemDriver itemManager,
            AuthenticationService authenticationService, EnvironmentReader environmentReader) {
        this.tableDriver = tableManager;
        this.itemDriver = itemManager;
        this.authenticationService = authenticationService;

        validationSchemaTableName =
                environmentReader.getEnvForName(EnvironmentReader.VALIDATION_SCHEMA_TABLE_NAME);
    }

    protected boolean createRegistryFromTemplate(RegistryEntryDto request)
            throws JsonProcessingException {
        String registryName = request.getId();
        String json = objectMapper.writeValueAsString(request);
        return createRegistryFromJson(registryName, json);
    }

    protected boolean createRegistryFromJson(String registryName, String json) {
        checkIfSchemaTableExistsOrCreate(registryName, validationSchemaTableName);
        checkIfRegistryExistsInSchemaTable(registryName, validationSchemaTableName);
        return createRegistryTable(registryName, json, validationSchemaTableName);
    }

    private boolean createRegistryTable(String registryName, String json, String schemaTable) {
        boolean created = tableDriver.createEntityRegistryTable(registryName);

        if (created) {
            addRegistryToSchemaTable(registryName, json, schemaTable);
            logger.info("Registry created successfully, registryId={}", registryName);
        }
        return created;
    }

    private void addRegistryToSchemaTable(String registryName, String json, String schemaTable) {
        itemDriver.addItem(schemaTable, registryName, json);
    }

    private void checkIfRegistryExistsInSchemaTable(String registryName, String schemaTable) {
        if (itemDriver.itemExists(schemaTable, registryName)) {
            String message = String.format(
                    "Registry already exists in schema table, registryId=%s, schemeTable=%s",
                    registryName, schemaTable);
            throw new RegistryAlreadyExistsException(message);
        }
    }

    private void checkIfSchemaTableExistsOrCreate(String registryName, String schemaTable) {
        if (!tableDriver.tableExists(schemaTable)) {
            logger.info(
                    "Schema table does not exist, creating new one, registryId={}, schemaTable={}",
                    registryName, schemaTable);
            tableDriver.createRegistryMetadataTable(schemaTable);
        }
    }

    public CreatedRegistryDto createRegistry(EntityRegistryTemplate template)
            throws JsonProcessingException {

        logger.info("Creating registry, template={}", template);

        String registryName = template.getId();

        if (registryExists(registryName)) {
            throw new RegistryAlreadyExistsException(registryName);
        } else {
            boolean registryCreated = createRegistryFromTemplate(template);

            if (registryCreated) {

                ApiKey apiKey = ApiKey.createRegistryAdminApiKey(registryName);
                String savedApiKey = authenticationService.saveApiKey(apiKey);

                return new CreatedRegistryDto(
                        String.format("A registry with name=%s is being created", registryName),
                        registryName, savedApiKey);

            }

            return new CreatedRegistryDto("Registry NOT created. See log for details");
        }
    }

    public boolean registryExists(String tableName) {
        return tableDriver.tableExists(tableName);
    }

    public Status validateRegistryExists(String tableName) {
        RegistryStatus status = status(tableName);
        switch (status) {
            case ACTIVE:
                return Status.CREATED;
            case CREATING:
            case UPDATING:
                throw new RegistryUnavailableException(tableName,
                        status.name().toLowerCase(Locale.ENGLISH));
            case DELETING:
            case NOT_FOUND:
            default:
                throw new RegistryNotFoundException(tableName);
        }
    }

    public void emptyRegistry(String tableName) {
        tableDriver.emptyEntityRegistryTable(tableName);
        tableDriver.createEntityRegistryTable(tableName);
    }

    public boolean deleteRegistry(String registryName) {

        logger.info("Deteleting registry, registryId={}", registryName);

        if (tableDriver.tableSize(registryName) > 0) {
            logger.warn("Can not delete registry that is not empty, registryId={}", registryName);
            return false;
        }

        tableDriver.deleteTable(registryName);
        boolean deleted = itemDriver.deleteItem(validationSchemaTableName, registryName);

        if (deleted) {
            authenticationService.deleteApiKeyForRegistry(registryName);
        }

        return deleted;
    }

    public Optional<String> getSchemaAsJson(String registryName) throws IOException {

        Optional<String> registrySchemaItem =
                itemDriver.getItem(validationSchemaTableName, registryName);

        Optional<String> schema = Optional.empty();

        if (registrySchemaItem.isPresent()) {
            EntityRegistryTemplate registryTemplate =
                    objectMapper.readValue(registrySchemaItem.get(), EntityRegistryTemplate.class);
            schema = Optional.ofNullable(registryTemplate.getSchema());
        }

        return schema;
    }

    public void setSchemaJson(String registryName, String schemaAsJson) throws IOException {
        Optional<String> registrySchemaItem =
                itemDriver.getItem(validationSchemaTableName, registryName);

        EntityRegistryTemplate registryTemplate =
                objectMapper.readValue(registrySchemaItem.get(), EntityRegistryTemplate.class);

        registryTemplate.setSchema(schemaAsJson);
        updateRegistryMetadata(registryTemplate);
    }

    public List<String> getRegistries() {
        List<String> tables = tableDriver.listTables();
        return tables.stream()
                .filter(tableName -> itemDriver.itemExists(validationSchemaTableName, tableName))
                .collect(Collectors.toList());
    }

    public EntityRegistryTemplate getRegistryMetadata(String registryName) throws IOException {

        EntityRegistryTemplate template = new EntityRegistryTemplate();
        Optional<String> entry = itemDriver.getItem(validationSchemaTableName, registryName);
        if (entry.isPresent()) {
            template = objectMapper.readValue(entry.get(), EntityRegistryTemplate.class);
            return template;
        } else {
            throw new RegistryNotFoundException(registryName, validationSchemaTableName);
        }

    }

    public void updateRegistryMetadata(EntityRegistryTemplate request)
            throws JsonProcessingException {

        String json = objectMapper.writeValueAsString(request);
        itemDriver.updateItem(validationSchemaTableName, request.getId(), json);

    }

    public RegistryStatus status(String registryName) {

        RegistryStatus registryStatus = RegistryStatus.valueOf(tableDriver.status(registryName));
        if (registryStatus == null) {
            registryStatus = RegistryStatus.NOT_FOUND;
        }

        return registryStatus;
    }

}
