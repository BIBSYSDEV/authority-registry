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
    
    public EntityDto addEntity(EntityDto dto) {
        Entity entity = entityManager.addEntity(EntityConverter.toEntity(dto));
        return EntityConverter.toEntityDto(entity);
    }
    
    public EntityDto getEntity(String entityId) {
        Entity entity = entityManager.getEntity(entityId);
        return EntityConverter.toEntityDto(entity);
    }
    
}
