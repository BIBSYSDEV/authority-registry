package no.bibsys.db;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import no.bibsys.db.exceptions.EntityNotFoundException;

public class EntityManager {

    private final transient ItemDriver itemManager;
    private final transient Logger logger = LoggerFactory.getLogger(EntityManager.class);
    
    public EntityManager(ItemDriver itemManager) {
        this.itemManager = itemManager;
    }
    
    public Optional<String> addEntity(final String registryName, final String json) throws IOException {
        
        String entityId = createEntityId();
        boolean addItemSuccess = itemManager.addItem(registryName, entityId, json);
        if(!addItemSuccess) {
            logger.info("Entity not created, entityId={}", entityId);
            return Optional.empty();
        }

        logger.info("Entity created successfully, entityId={}");
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
        logger.info("Trying to create entity with entityId={}", entitiyId);
        return entitiyId;
    }
    
    public void validateItemExists(String registryName, String entityId) {
    	if (!entityExists(registryName, entityId)) {
    		throw new EntityNotFoundException(registryName, entityId);
    	}
    }
}
