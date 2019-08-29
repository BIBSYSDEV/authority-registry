package no.bibsys.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.bibsys.db.EntityManager;
import no.bibsys.db.structures.Entity;
import no.bibsys.entitydata.validation.DataValidator;
import no.bibsys.entitydata.validation.exceptions.EntityFailedShaclValidationException;
import no.bibsys.service.exceptions.ValidationSchemaNotFoundException;
import no.bibsys.utils.ModelParser;
import no.bibsys.web.model.EntityConverter;
import no.bibsys.web.model.EntityDto;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;

import java.io.IOException;
import java.util.Objects;

public class EntityService extends ModelParser {

    private static final Lang VALIDATION_SCHEMA_LANGUAGE = Lang.JSONLD;
    private static final String VALIDATION_SCHEMA_NOT_FOUND = "Validation schema not found for registry: %s";
    private final transient EntityManager entityManager;
    private final transient RegistryService registryService;

    public EntityService(EntityManager entityManager, RegistryService registryService) {
        super();
        this.entityManager = entityManager;
        this.registryService = registryService;
    }

    public EntityDto addEntity(String uri, String registryId, EntityDto entityDto)
            throws EntityFailedShaclValidationException, ValidationSchemaNotFoundException, IOException {
        validateEntity(registryId, entityDto);
        return addEntityToRegistry(uri, registryId, entityDto);
    }

    public EntityDto updateEntity(String registryId, EntityDto entityDto)
        throws ValidationSchemaNotFoundException, EntityFailedShaclValidationException, JsonProcessingException {
        validateEntity(registryId, entityDto);
        return updateEntityInRegistry(registryId, entityDto);
    }

    public EntityDto getEntity(String registryId, String entityId) throws JsonProcessingException {
        Entity entity = entityManager.getEntity(registryId, entityId);
        return EntityConverter.toEntityDto(entity);
    }

    public void deleteEntity(String registryId, String entityId) {
        entityManager.deleteEntity(registryId, entityId);
    }

    private void validateEntity(String registryId, EntityDto entityDto)
        throws EntityFailedShaclValidationException, ValidationSchemaNotFoundException {
        String validationSchema = registryService.getRegistry(registryId).getSchema();
        if (Objects.isNull(validationSchema)) {
            throw new ValidationSchemaNotFoundException(String.format(VALIDATION_SCHEMA_NOT_FOUND, registryId));
        }

        DataValidator dataValidator = new DataValidator(parseModel(validationSchema, VALIDATION_SCHEMA_LANGUAGE));
        Model dataModel = parseModel(entityDto.getBody(), Lang.JSONLD);
        dataValidator.isValidEntry(dataModel);
    }

    private EntityDto updateEntityInRegistry(String registryId, EntityDto entityDto) throws JsonProcessingException {
        Entity entity = entityManager.updateEntity(registryId, EntityConverter.toEntity(entityDto));
        return EntityConverter.toEntityDto(entity);
    }

    private EntityDto addEntityToRegistry(String uri, String registryId, EntityDto entityDto)
            throws IOException {
        Entity entity = entityManager.addEntity(registryId, EntityConverter.toEntity(uri, entityDto));
        return EntityConverter.toEntityDto(entity);
    }
}
