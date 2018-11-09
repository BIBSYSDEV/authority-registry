package no.bibsys.db;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EntityManager {

    private final transient ItemDriver itemManager;

    public EntityManager(ItemDriver itemManager) {
        this.itemManager = itemManager;
    }
    
    public Optional<String> addEntity(final String registryName, final String json) throws IOException {
        
        String entityId = createEntityId();
        String updatedJson = addIdToJson(json, entityId);
        itemManager.addItem(registryName, updatedJson);

        return Optional.ofNullable(entityId);

    }

    public Optional<String> getEntity(String registryName, String id) {
        return itemManager.getItem(registryName, id);
    }

    public void deleteEntity(String registryName, String entityId) {

        itemManager.deleteItem(registryName, entityId);
        
    }

    public Optional<String> updateEntity(String registryName, String entity) {
        return itemManager.updateItem(registryName, entity);
    }

    public boolean entityExists(String registryName, String entity) {
        return itemManager.itemExists(registryName, entity);
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
