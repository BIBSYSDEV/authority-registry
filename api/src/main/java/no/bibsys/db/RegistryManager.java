package no.bibsys.db;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import no.bibsys.db.structures.EntityRegistryTemplate;

public class RegistryManager {

    private final transient TableDriver tableDriver;
    private final transient ItemDriver itemDriver;
    private final transient ObjectMapper objectMapper = ObjectMapperHelper.getObjectMapper();

    public RegistryManager(TableDriver tableManager, ItemDriver itemManager) {
        this.tableDriver = tableManager;
        this.itemDriver = itemManager;
    }

    public boolean createRegistryFromTemplate(EntityRegistryTemplate request) throws JsonProcessingException {
        String registryName = request.getId();
        String json = objectMapper.writeValueAsString(request);
        return createRegistryFromJson(registryName, json);
    }

    public boolean createRegistryFromJson(String registryName, String json) throws JsonProcessingException{

        if(!tableDriver.tableExists(getValidationSchemaTable())) {
            tableDriver.createTable(getValidationSchemaTable());
        }

        if(itemDriver.itemExists(getValidationSchemaTable(), registryName)) {
            return false;
        }
        
        itemDriver.addItem(getValidationSchemaTable(), json);
        return tableDriver.createTable(registryName);
    }

    public boolean registryExists(String tableName) {
        return tableDriver.tableExists(tableName);
    }

    public void emptyRegistry(String tableName) {
        tableDriver.emptyTable(tableName);
        tableDriver.createTable(tableName);
    }

    public boolean deleteRegistry(String tableName) {

        if(tableDriver.tableSize(tableName) > 0) {
            return false;
        }
        
        tableDriver.deleteTable(tableName);
        itemDriver.deleteItem(getValidationSchemaTable(), tableName);
        return true;
    }

    public Optional<String> getSchemaAsJson(String registryName) throws IOException {

        Optional<String> registrySchemaItem = itemDriver.getItem(getValidationSchemaTable(), registryName);

        ObjectMapper mapper = new ObjectMapper();
        EntityRegistryTemplate registryTemplate = mapper.readValue(registrySchemaItem.get(), EntityRegistryTemplate.class);

        String schema = Optional.ofNullable(registryTemplate.getSchema()).orElse("");
        return Optional.ofNullable(schema);
    }

    public void setSchemaJson(String registryName, String schemaAsJson) throws JsonParseException, JsonMappingException, IOException {
        Optional<String> registrySchemaItem = itemDriver.getItem(getValidationSchemaTable(), registryName);

        ObjectMapper mapper = new ObjectMapper();
        EntityRegistryTemplate registryTemplate = mapper.readValue(registrySchemaItem.get(), EntityRegistryTemplate.class);

        registryTemplate.setSchema(schemaAsJson);
        updateRegistryMetadata(registryTemplate);
    }

    public List<String> getRegistries() {
        List<String> tables = tableDriver.listTables();
        return tables.stream()
                .filter(tableName -> itemDriver.itemExists(getValidationSchemaTable(), tableName))
                .collect(Collectors.toList());
    }

    public EntityRegistryTemplate getRegistryMetadata(String registryName) throws IOException {

        EntityRegistryTemplate template = new EntityRegistryTemplate();

        Optional<String> entry = itemDriver.getItem(getValidationSchemaTable(), registryName);
        ObjectMapper mapper = new ObjectMapper();
        template = mapper.readValue(entry.get() , EntityRegistryTemplate.class);

        return template;
    }

    public void updateRegistryMetadata(EntityRegistryTemplate request) throws JsonProcessingException {

        String json = objectMapper.writeValueAsString(request);
        itemDriver.updateItem(getValidationSchemaTable(), json);

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
