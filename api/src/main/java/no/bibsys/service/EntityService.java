package no.bibsys.service;

import no.bibsys.db.EntityManager;
import no.bibsys.db.structures.Entity;
import no.bibsys.entitydata.validation.DataValidator;
import no.bibsys.entitydata.validation.ModelParser;
import no.bibsys.entitydata.validation.exceptions.EntryFailedShaclValidationException;
import no.bibsys.web.model.EntityConverter;
import no.bibsys.web.model.EntityDto;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;


public class EntityService extends ModelParser {

    public static final Lang VALIDATION_SCHEMA_LANGUAGE=Lang.JSONLD;
    private final transient EntityManager entityManager;
    private final transient RegistryService registryService;


    public EntityService(EntityManager entityManager, RegistryService registryService) {
        super();
        this.entityManager = entityManager;
        this.registryService=registryService;
    }
    
    public EntityDto addEntity(String registryId, EntityDto entityDto)throws EntryFailedShaclValidationException {
        String validationSchema=registryService.getRegistry(registryId).getSchema();
        DataValidator dataValidator=new DataValidator(parseModel(validationSchema,VALIDATION_SCHEMA_LANGUAGE));
        Model dataModel=parseModel(entityDto.getBody(),VALIDATION_SCHEMA_LANGUAGE);
        if(dataValidator.isValidEntry(dataModel)){
            Entity entity = entityManager.addEntity(registryId, EntityConverter.toEntity(entityDto));
            return EntityConverter.toEntityDto(entity);
        }
        else{
            throw  new EntryFailedShaclValidationException();
        }

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
