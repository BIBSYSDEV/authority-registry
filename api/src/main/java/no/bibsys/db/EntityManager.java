package no.bibsys.db;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class EntityManager {

    private final transient ItemDriver itemManager;

    public EntityManager(ItemDriver itemManager) {
        this.itemManager = itemManager;
    }
    
    public Optional<String> addEntity(final String registryName, final String json) throws IOException {
        
        String entityId = createEntityId();
        boolean addItemSuccess = itemManager.addItem(registryName, entityId, json);
        if(!addItemSuccess) {
            return Optional.empty();
        }

        return Optional.ofNullable(entityId);

    }

    public Optional<String> getEntity(String registryName, String id) {
        return itemManager.getItem(registryName, id);
    }

    public boolean deleteEntity(String registryName, String entityId) {

        return itemManager.deleteItem(registryName, entityId);
        
    }

    public Optional<String> updateEntity(String registryName, String entityId, String entity) {
        return itemManager.updateItem(registryName, entityId, entity);
    }

    public boolean entityExists(String registryName, String entityId) {
        return itemManager.itemExists(registryName, entityId);
    }
    
    private String createEntityId() {
        String entitiyId = UUID.randomUUID().toString();
        return entitiyId;
    }
}
