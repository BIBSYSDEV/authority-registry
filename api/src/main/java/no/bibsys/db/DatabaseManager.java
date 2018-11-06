package no.bibsys.db;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import com.amazonaws.services.dynamodbv2.model.TableNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DatabaseManager {

    private final transient TableDriver tableDriver;

    public DatabaseManager(TableDriver tableDriver) {
        this.tableDriver = tableDriver;
    }

    
    public String addEntry(final String tableName, final String json) throws IOException {
        
        if (tableExists(tableName)) {
            String entityId = createEntityId();
            String updatedJson = addIdToJson(json, entityId);
            EntityManager tableWriter = new EntityManager(tableDriver, tableName);
            tableWriter.addJson(updatedJson);
            
            return entityId;
        } else {
            throw new TableNotFoundException(String.format("Registry %s does not exist", tableName));
        }

    }

    private String addIdToJson(final String json, final String entityId) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode tree = objectMapper.readTree(json);
        ((ObjectNode)tree).put("id", entityId);
        String updatedJson = tree.toString();
        return updatedJson;
    }

    private String createEntityId() {
        String entitiyId = UUID.randomUUID().toString();
        return entitiyId;
    }

    public Optional<String> getEntry(String tableName, String id) {
        if (tableExists(tableName)) {
            EntityManager entityManager = new EntityManager(tableDriver, tableName);
            return entityManager.getEntry(id);
        } else {
            throw new TableNotFoundException(String.format("Registry %s does not exist", tableName));
        }

    }

    public void deleteEntity(String registryName, String entityId) {

        EntityManager entityManager = new EntityManager(tableDriver, registryName);
        entityManager.deleteEntry(entityId);
        
    }

    public String updateEntity(String registryName, String entity) {
        EntityManager entityManager = new EntityManager(tableDriver, registryName);
        return entityManager.updateJson(entity);
    }

    public boolean tableExists(String tableName) {
        return new TableManager(tableDriver).tableExists(tableName);
    }
}
