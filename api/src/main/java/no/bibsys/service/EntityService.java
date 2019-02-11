package no.bibsys.service;

import java.util.Objects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;

import com.fasterxml.jackson.core.JsonProcessingException;

import no.bibsys.db.EntityManager;
import no.bibsys.db.structures.Entity;
import no.bibsys.entitydata.validation.DataValidator;
import no.bibsys.entitydata.validation.exceptions.EntityFailedShaclValidationException;
import no.bibsys.service.exceptions.ValidationSchemaNotFoundException;
import no.bibsys.utils.ModelParser;
import no.bibsys.web.model.EntityConverter;
import no.bibsys.web.model.EntityDto;

public class EntityService extends ModelParser {

    private static final Lang VALIDATION_SCHEMA_LANGUAGE = Lang.JSONLD;
    private static final String VALIDATION_SCHEMA_NOT_FOUND = "Validation schema not found for registry: %s";
    private static final String FAILURE_ADDING_ENTITY = "Failed to add entity to registry %s ";
    private static final String FAILURE_UPDATING_REGISTRY = "Failed to update entity in registry: %s";
    private final transient EntityManager entityManager;
    private final transient RegistryService registryService;

    public EntityService(EntityManager entityManager, RegistryService registryService) {
        super();
        this.entityManager = entityManager;
        this.registryService = registryService;
    }

    public EntityDto addEntity(String registryId, EntityDto entityDto)
        throws EntityFailedShaclValidationException, ValidationSchemaNotFoundException, JsonProcessingException {
        if (validateEntity(registryId, entityDto)) {
            return addEntityToRegistry(registryId, entityDto);
        } else {
            throw new IllegalStateException(String.format(FAILURE_ADDING_ENTITY, registryId));
        }
    }

    public EntityDto updateEntity(String registryId, EntityDto entityDto)
        throws ValidationSchemaNotFoundException, EntityFailedShaclValidationException, JsonProcessingException {
        if (validateEntity(registryId, entityDto)) {
            return updateEntityInRegistry(registryId, entityDto);
        } else {
            throw new IllegalStateException(String.format(FAILURE_UPDATING_REGISTRY, registryId));
        }
    }

    public EntityDto getEntity(String registryId, String entityId) throws JsonProcessingException {
        Entity entity = entityManager.getEntity(registryId, entityId);
        return EntityConverter.toEntityDto(entity);
    }

    public void deleteEntity(String registryId, String entityId) {
        entityManager.deleteEntity(registryId, entityId);
    }

    private boolean validateEntity(String registryId, EntityDto entityDto)
        throws EntityFailedShaclValidationException, ValidationSchemaNotFoundException {
        String validationSchema = registryService.getRegistry(registryId).getSchema();
        if (Objects.isNull(validationSchema)) {
            throw new ValidationSchemaNotFoundException(String.format(VALIDATION_SCHEMA_NOT_FOUND, registryId));
        }

        DataValidator dataValidator = new DataValidator(parseModel(validationSchema, VALIDATION_SCHEMA_LANGUAGE));
        Model dataModel = parseModel(entityDto.getBody(), VALIDATION_SCHEMA_LANGUAGE);
        return dataValidator.isValidEntry(dataModel);
    }

    private EntityDto updateEntityInRegistry(String registryId, EntityDto entityDto) throws JsonProcessingException {
        Entity entity = entityManager.updateEntity(registryId, EntityConverter.toEntity(entityDto));
        return EntityConverter.toEntityDto(entity);
    }

    private EntityDto addEntityToRegistry(String registryId, EntityDto entityDto) throws JsonProcessingException {
        Entity entity = entityManager.addEntity(registryId, EntityConverter.toEntity(entityDto));
        return EntityConverter.toEntityDto(entity);
    }
}
