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

public class RegistryManager {

    private final transient TableDriver tableManager;
    private final transient ItemDriver itemManager;
    private final transient ObjectMapper objectMapper = ObjectMapperHelper.getObjectMapper();

    public RegistryManager(TableDriver tableManager, ItemDriver itemManager) {
        this.tableManager = tableManager;
        this.itemManager = itemManager;
    }

    public void createRegistry(EntityRegistryTemplate request) throws JsonProcessingException {
        String registryName = request.getId();
        String json = objectMapper.writeValueAsString(request);
        addRegistry(registryName, json);
    }

    public void addRegistry(String registryName, String json) throws JsonProcessingException{

        tableManager.createTable(getValidationSchemaTable());

        itemManager.addItem(getValidationSchemaTable(), json);
        tableManager.createTable(registryName);

    }

    public boolean registryExists(String tableName) {
        return tableManager.tableExists(tableName);
    }

    public void emptyRegistry(String tableName) {
        tableManager.deleteTable(tableName);
        tableManager.createTable(tableName);
    }

    public void deleteRegistry(String tableName) {

        itemManager.deleteItem(getValidationSchemaTable(), tableName);
        tableManager.deleteTable(tableName);
    }

    public Optional<String> getSchemaAsJson(String registryName) throws JsonParseException, JsonMappingException, IOException, InterruptedException {

        Optional<String> registrySchemaItem = itemManager.getItem(getValidationSchemaTable(), registryName);

        ObjectMapper mapper = new ObjectMapper();
        EntityRegistryTemplate registryTemplate = mapper.readValue(registrySchemaItem.get(), EntityRegistryTemplate.class);

        String schema = Optional.ofNullable(registryTemplate.getSchema()).orElse("");
        return Optional.ofNullable(schema);
    }

    public void setSchemaJson(String registryName, String schemaAsJson) throws JsonParseException, JsonMappingException, IOException, InterruptedException {
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
        itemManager.updateItem(getValidationSchemaTable(), json);

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
