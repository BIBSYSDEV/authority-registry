package no.bibsys.db.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String registryId, String entityId) {
        super(String.format("Entity with id %s does not exist in registry %s", entityId,
                registryId));
    }

}
