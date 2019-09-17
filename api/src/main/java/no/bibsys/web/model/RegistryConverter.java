package no.bibsys.web.model;

import no.bibsys.db.structures.Registry;

public class RegistryConverter extends BaseConverter {

    public static RegistryDto toRegistryDto(Registry registry) {
        RegistryDto dto = new RegistryDto();
        dto.setId(registry.getId());
        dto.setMetadata(toMap(registry.getMetadata()));
        dto.setSchema(registry.getSchema());
        dto.setUiSchema(registry.getUiSchema());
        return dto;
    }
    
    public static Registry toRegistry(RegistryDto dto) {
        Registry registry = new Registry();
        registry.setId(dto.getId());
        registry.setMetadata(toObjectNode(dto.getMetadata()));
        registry.setSchema(dto.getSchema());
        registry.setUiSchema(dto.getUiSchema());
        return registry;
    }
    
}
