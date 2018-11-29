package no.bibsys.web.model;

import no.bibsys.db.structures.Registry;

public class RegistryConverter {

    public static RegistryDto toRegistryDto(Registry registry) {
        RegistryDto dto = new RegistryDto();
        dto.setId(registry.getId());
        dto.setMetadata(registry.getMetadata());
        dto.setSchema(registry.getSchema());
        return dto;
    }
    
    public static Registry toRegistry(RegistryDto dto) {
        Registry registry = new Registry();
        registry.setId(dto.getId());
        registry.setMetadata(dto.getMetadata());
        registry.setSchema(dto.getSchema());
        return registry;
    }
    
}
