package no.bibsys.db;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import no.bibsys.web.exception.RegistryNotFoundException;

public class EntityManager {

    private final transient TableManager tableManager;
    private final transient ItemManager itemManager;

    public EntityManager(TableManager tableManager, ItemManager itemManager) {
        this.tableManager = tableManager;
        this.itemManager = itemManager;
    }
    
    public String addEntity(final String registryName, final String json) throws IOException {
        
        if (tableExists(registryName)) {
            String entityId = createEntityId();
            String updatedJson = addIdToJson(json, entityId);
            itemManager.addJson(registryName, updatedJson);
            
            return entityId;
        } else {
            throw new RegistryNotFoundException(String.format("Registry %s does not exist", registryName));
        }

    }

    public Optional<String> getEntity(String registryName, String id) {
        if (tableExists(registryName)) {
            return itemManager.getItem(registryName, id);
        } else {
            throw new RegistryNotFoundException(String.format("Registry %s does not exist", registryName));
        }

    }

    public void deleteEntity(String registryName, String entityId) {

        itemManager.deleteEntry(registryName, entityId);
        
    }

    public Optional<String> updateEntity(String registryName, String entity) {
        return itemManager.updateJson(registryName, entity);
    }

    private boolean tableExists(String tableName) {
        return tableManager.tableExists(tableName);
    }

    private String addIdToJson(final String json, final String entityId) throws IOException {
        ObjectMapper objectMapper = ObjectMapperHelper.getObjectMapper();
        JsonNode tree = objectMapper.readTree(json);
        ((ObjectNode)tree).put("id", entityId);
        String updatedJson = objectMapper.writeValueAsString(tree);
        return updatedJson;
    }

    private String createEntityId() {
        String entitiyId = UUID.randomUUID().toString();
        return entitiyId;
    }
}
