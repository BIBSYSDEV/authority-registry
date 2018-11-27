package no.bibsys.db;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import no.bibsys.web.exception.EntityNotFoundException;

import javax.ws.rs.core.Response.Status;

public class EntityManager {

    private final transient ItemDriver itemDriver;
    private static final Logger logger = LoggerFactory.getLogger(EntityManager.class);

    public EntityManager(ItemDriver itemDriver) {
        this.itemDriver = itemDriver;
    }

    public Optional<String> addEntity(final String registryName, final String json)
            throws IOException {

        String entityId = createEntityId();
        boolean addItemSuccess = itemDriver.addItem(registryName, entityId, json);
        if (!addItemSuccess) {
        	logger.error("Entity not created, registryId={}, entityId={}", registryName, entityId);
            return Optional.empty();
        }

        logger.info("Entity created successfully, registryId={}, entityId={}", registryName,
                entityId);
        return Optional.ofNullable(entityId);

    }

    public Optional<String> getEntity(String registryName, String id) {
        return itemDriver.getItem(registryName, id);
    }

    public boolean deleteEntity(String registryName, String entityId) {
        return itemDriver.deleteItem(registryName, entityId);

    }

    public Optional<String> updateEntity(String registryName, String entityId, String entity) {
        return itemDriver.updateItem(registryName, entityId, entity);
    }

    public boolean entityExists(String registryName, String entityId) {
        return itemDriver.itemExists(registryName, entityId);
    }

    private String createEntityId() {
        String entitiyId = UUID.randomUUID().toString();
        return entitiyId;
    }

    public Status validateItemExists(String registryName, String entityId) {
        if (!entityExists(registryName, entityId)) {
            throw new EntityNotFoundException(registryName, entityId);
        }

        return Status.CREATED;
    }
}
