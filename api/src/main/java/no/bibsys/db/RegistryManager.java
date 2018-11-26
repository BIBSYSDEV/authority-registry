package no.bibsys.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import no.bibsys.EnvironmentReader;
import no.bibsys.db.structures.EntityRegistryTemplate;
import no.bibsys.service.ApiKey;
import no.bibsys.service.AuthenticationService;
import no.bibsys.web.exception.RegistryAlreadyExistsException;
import no.bibsys.web.exception.RegistryNotFoundException;
import no.bibsys.web.model.CreatedRegistry;

public class RegistryManager {

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

    protected boolean createRegistryFromTemplate(EntityRegistryTemplate request)
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
        boolean created = tableDriver.createTable(registryName);

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
            tableDriver.createTable(schemaTable);
        }
    }

    public CreatedRegistry createRegistry(EntityRegistryTemplate template)
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

                return new CreatedRegistry(
                        String.format("A registry with name=%s has been created", registryName),
                        registryName, savedApiKey);

            }

            return new CreatedRegistry("Registry NOT created. See log for details");
        }
    }

    public boolean registryExists(String tableName) {
        return tableDriver.tableExists(tableName);
    }

    public void validateRegistryExists(String tableName) {
        if (!registryExists(tableName)) {
            throw new RegistryNotFoundException(tableName);
        }
    }

    public void emptyRegistry(String tableName) {
        tableDriver.emptyTable(tableName);
        tableDriver.createTable(tableName);
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
            throw new RegistryNotFoundException(registryName);
        }

    }

    public void updateRegistryMetadata(EntityRegistryTemplate request)
            throws JsonProcessingException {

        String json = objectMapper.writeValueAsString(request);
        itemDriver.updateItem(validationSchemaTableName, request.getId(), json);

    }

}
