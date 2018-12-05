package no.bibsys.web.model;

import no.bibsys.db.structures.Entity;

public class EntityConverter extends BaseConverter {
        
    public static EntityDto toEntityDto(Entity entity) {
        EntityDto dto = new EntityDto();
        dto.setId(entity.getId());
        dto.setCreated(entity.getCreated());
        dto.setModified(entity.getModified());
        dto.setBody(toJsonNode(entity.getBody()));
        return dto;
    }
    
    public static Entity toEntity(EntityDto dto) {
        Entity entity = new Entity();
        entity.setId(dto.getId());
        entity.setCreated(dto.getCreated());
        entity.setModified(dto.getModified());
        entity.setBody(toObjectNode(dto.getBody()));
        return entity;
    }
    
}
