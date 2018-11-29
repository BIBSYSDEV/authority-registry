package no.bibsys.web.model;

import no.bibsys.db.structures.RegistryEntry;

public class RegistryEntryConverter {

    public static RegistryEntryDto toEntityRegistryDto(RegistryEntry registry) {
        RegistryEntryDto dto = new RegistryEntryDto();
        dto.setId(registry.getId());
        dto.setMetadata(registry.getMetadata());
        dto.setSchema(registry.getSchema());
        return dto;
    }
    
    public static RegistryEntry toEntityRegistry(RegistryEntryDto dto) {
        RegistryEntry registry = new RegistryEntry();
        registry.setId(dto.getId());
        registry.setMetadata(dto.getMetadata());
        registry.setSchema(dto.getSchema());
        return registry;
    }
    
}
