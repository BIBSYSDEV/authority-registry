package no.bibsys.db;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.amazonaws.services.dynamodbv2.model.TableNotFoundException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.bibsys.db.structures.EntityRegistryTemplate;

public class RegistryManager {

    private final transient TableDriver tableDriver;

    public RegistryManager(TableDriver tableDriver) {
        this.tableDriver = tableDriver;
    }
    
    public void createRegistry(EntityRegistryTemplate request)
            throws InterruptedException, TableAlreadyExistsException, JsonProcessingException {
        TableManager tableManager = new TableManager(tableDriver);

        String tableName = request.getId();
        if (registryExists(tableName)) {
            throw new TableAlreadyExistsException(String.format("Registry %s already exists", tableName));
        } else {
            tableManager.createRegistry(request);
        }
    }
    
    public void addRegistry(String tableName, String json) throws JsonProcessingException, InterruptedException {
        if (!registryExists(tableName)) {
            EntityManager tableWriter = new EntityManager(tableDriver, tableName);
            tableWriter.addJson(json);
        } else {
            throw new TableAlreadyExistsException(String.format("Registry %s already exists", tableName));
        }
        
    }
    
    public boolean registryExists(String tableName) throws InterruptedException {
        return new TableManager(tableDriver).registryExists(tableName);
    }

    public void emptyRegistry(String tableName) throws InterruptedException {
        TableManager tableManager = new TableManager(tableDriver);
        tableManager.emptyTable(tableName);

    }

    public void deleteRegistry(String tableName) throws InterruptedException {
        TableManager tableManager = new TableManager(tableDriver);
        if (registryExists(tableName)) {
            
            EntityManager schemaTableWriter = new EntityManager(tableDriver, TableManager.getValidationSchemaTable());
            schemaTableWriter.deleteEntry(tableName);
            
            tableManager.deleteTable(tableName);
        } else {
            throw new TableNotFoundException(String.format("Registry %s does not exist", tableName));
        }
    }

    public String getSchemaAsJson(String registryName) throws JsonParseException, JsonMappingException, IOException, InterruptedException {

        if(!registryExists(registryName)) {
            throw new TableNotFoundException(String.format("Could not find a registry with name %s", registryName));
        }
        
        EntityManager entityManager = new EntityManager(tableDriver, TableManager.getValidationSchemaTable());
        Optional<String> entry = entityManager.getEntry(registryName);

        ObjectMapper mapper = new ObjectMapper();
        EntityRegistryTemplate registryTemplate = mapper.readValue(entry.get(), EntityRegistryTemplate.class);
        
        String schema = Optional.ofNullable(registryTemplate.getSchema()).orElse("");
        return schema;
    }
    
    public void setSchemaJson(String registryName, String schemaAsJson) throws JsonParseException, JsonMappingException, IOException, InterruptedException {
        if(!registryExists(registryName)) {
            throw new TableNotFoundException(String.format("Could not find a registry with name %s", registryName));
        }
        
        EntityManager entityManager = new EntityManager(tableDriver, TableManager.getValidationSchemaTable());
        Optional<String> entry = entityManager.getEntry(registryName);

        ObjectMapper mapper = new ObjectMapper();
        EntityRegistryTemplate registryTemplate = mapper.readValue(entry.get(), EntityRegistryTemplate.class);

        registryTemplate.setSchema(schemaAsJson);
        updateRegistry(registryTemplate);
    }

    public List<String> getTables() {
        
        TableManager tableManager = new TableManager(tableDriver);
        return tableManager.listAllTables();
    }
    
    public List<String> getRegistries() {
        TableManager tableManager = new TableManager(tableDriver);
        return tableManager.listRegistries();
    }

    public EntityRegistryTemplate getRegistryMetadata(String registryName) throws JsonParseException, JsonMappingException, IOException {
        
        EntityRegistryTemplate template = new EntityRegistryTemplate();
        
        EntityManager entityManager = new EntityManager(tableDriver, TableManager.getValidationSchemaTable());
        Optional<String> entry = entityManager.getEntry(registryName);
        ObjectMapper mapper = new ObjectMapper();
        template = mapper.readValue(entry.get() , EntityRegistryTemplate.class);
        
        return template;
    }

    public void updateRegistry(EntityRegistryTemplate request) throws TableNotFoundException, JsonProcessingException {
        
        TableManager tableManager = new TableManager(tableDriver);
        tableManager.updateRegistryMetadata(request);
        
    }
}
