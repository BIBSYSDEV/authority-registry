package no.bibsys.db;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.bibsys.db.structures.EntityRegistryTemplate;
import no.bibsys.web.exception.RegistryAlreadyExistsException;
import no.bibsys.web.exception.RegistryNotFoundException;

public class RegistryManager {

    private final transient TableManager tableManager;
    private final transient ItemManager itemManager;
    private final transient ObjectMapper objectMapper = ObjectMapperHelper.getObjectMapper();

    public RegistryManager(TableManager tableManager, ItemManager itemManager) {
        this.tableManager = tableManager;
        this.itemManager = itemManager;
    }

    public void createRegistry(EntityRegistryTemplate request) throws RegistryAlreadyExistsException, JsonProcessingException, InterruptedException {
        String registryName = request.getId();
        String json = objectMapper.writeValueAsString(request);
        addRegistry(registryName, json);
    }

    public void addRegistry(String registryName, String json) throws JsonProcessingException, InterruptedException{

        if(!tableManager.tableExists(getValidationSchemaTable())) {
            tableManager.createTable(getValidationSchemaTable());
        }

        if (!registryExists(registryName)) {
            itemManager.addJson(getValidationSchemaTable(), json);
            tableManager.createTable(registryName);
        } else {
            throw new RegistryAlreadyExistsException(String.format("Registry %s already exists", registryName));
        }

    }

    public boolean registryExists(String tableName) {
        return tableManager.tableExists(tableName);
    }

    public void emptyRegistry(String tableName) {
        tableManager.emptyTable(tableName);
    }

    public void deleteRegistry(String tableName) {
        if (registryExists(tableName)) {

            itemManager.deleteEntry(getValidationSchemaTable(), tableName);

            tableManager.deleteTable(tableName);
        } else {
            throw new RegistryNotFoundException(String.format("Registry %s does not exist", tableName));
        }
    }

    public String getSchemaAsJson(String registryName) throws JsonParseException, JsonMappingException, IOException, InterruptedException {

        if(!registryExists(registryName)) {
            throw new RegistryNotFoundException(String.format("Could not find a registry with name %s", registryName));
        }

        Optional<String> registrySchemaItem = itemManager.getItem(getValidationSchemaTable(), registryName);

        ObjectMapper mapper = new ObjectMapper();
        EntityRegistryTemplate registryTemplate = mapper.readValue(registrySchemaItem.get(), EntityRegistryTemplate.class);

        String schema = Optional.ofNullable(registryTemplate.getSchema()).orElse("");
        return schema;
    }

    public void setSchemaJson(String registryName, String schemaAsJson) throws JsonParseException, JsonMappingException, IOException, InterruptedException {
        if(!registryExists(registryName)) {
            throw new RegistryNotFoundException(String.format("Could not find a registry with name %s", registryName));
        }

        Optional<String> registrySchemaItem = itemManager.getItem(getValidationSchemaTable(), registryName);

        ObjectMapper mapper = new ObjectMapper();
        EntityRegistryTemplate registryTemplate = mapper.readValue(registrySchemaItem.get(), EntityRegistryTemplate.class);

        registryTemplate.setSchema(schemaAsJson);
        updateRegistry(registryTemplate);
    }

    public List<String> getRegistries() {
        List<String> tables = tableManager.listTables();
        return tables.stream()
                .filter(tableName -> itemManager.itemExists(getValidationSchemaTable(), tableName))
                .collect(Collectors.toList());
    }

    public EntityRegistryTemplate getRegistryMetadata(String registryName) throws JsonParseException, JsonMappingException, IOException {

        EntityRegistryTemplate template = new EntityRegistryTemplate();

        Optional<String> entry = itemManager.getItem(getValidationSchemaTable(), registryName);
        ObjectMapper mapper = new ObjectMapper();
        template = mapper.readValue(entry.get() , EntityRegistryTemplate.class);

        return template;
    }

    public void updateRegistry(EntityRegistryTemplate request) throws JsonProcessingException {

        String json = objectMapper.writeValueAsString(request);
        itemManager.updateJson(getValidationSchemaTable(), json);

    }

    private static String getValidationSchemaTable() {
        String validationSchemaTableName = "VALIDATION_SCHEMA_TABLE";
        String stage = System.getenv("STAGE_NAME");
        if("test".equals(stage)) {
            validationSchemaTableName = String.join("_", "TEST", validationSchemaTableName);
        }

        return validationSchemaTableName;
    }

}
