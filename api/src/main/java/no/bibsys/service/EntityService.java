package no.bibsys.service;

import no.bibsys.db.EntityManager;
import no.bibsys.db.structures.Entity;
import no.bibsys.web.model.EntityConverter;
import no.bibsys.web.model.EntityDto;

public class EntityService {

    private final EntityManager entityManager;
    
    public EntityService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public EntityDto addEntity(String registryId, EntityDto entityDto) {
        Entity entity = entityManager.addEntity(registryId, EntityConverter.toEntity(entityDto));
        return EntityConverter.toEntityDto(entity);
    }
    
    public EntityDto getEntity(String registryId, String entityId) {
        Entity entity = entityManager.getEntity(registryId, entityId);
        return EntityConverter.toEntityDto(entity);
    }
    
    public void deleteEntity(String registryId, String entityId) {
        entityManager.deleteEntity(registryId, entityId);
    }
    
    public EntityDto updateEntity(String registryId, EntityDto entityDto) {
        Entity entity = entityManager.updateEntity(registryId, EntityConverter.toEntity(entityDto));
        return EntityConverter.toEntityDto(entity);
    }
}
