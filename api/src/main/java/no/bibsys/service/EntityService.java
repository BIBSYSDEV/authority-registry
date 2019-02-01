package no.bibsys.service;

import java.util.function.BiFunction;
import no.bibsys.db.EntityManager;
import no.bibsys.db.structures.Entity;
import no.bibsys.entitydata.validation.DataValidator;
import no.bibsys.entitydata.validation.ModelParser;
import no.bibsys.entitydata.validation.exceptions.EntityFailedShaclValidationException;
import no.bibsys.service.exceptions.ValidationSchemaNotFoundException;
import no.bibsys.web.model.EntityConverter;
import no.bibsys.web.model.EntityDto;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;

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

    public EntityDto addEntity(String registryId, EntityDto entityDto)
        throws EntityFailedShaclValidationException, ValidationSchemaNotFoundException {
        return addUpdateEntity(registryId, entityDto, this::addEntityToRegistry);
    }

    public EntityDto updateEntity(String registryId, EntityDto entityDto)
        throws ValidationSchemaNotFoundException, EntityFailedShaclValidationException {
        return addUpdateEntity(registryId, entityDto, this::updateEntityInRegistry);
    }

    public EntityDto getEntity(String registryId, String entityId) {
        Entity entity = entityManager.getEntity(registryId, entityId);
        return EntityConverter.toEntityDto(entity);
    }

    public void deleteEntity(String registryId, String entityId) {
        entityManager.deleteEntity(registryId, entityId);
    }

    private EntityDto addUpdateEntity(String registryId, EntityDto entityDto,
        BiFunction<String, EntityDto, EntityDto> action)
        throws ValidationSchemaNotFoundException, EntityFailedShaclValidationException {
        String validationSchema = registryService.getRegistry(registryId).getSchema();
        if (validationSchema == null) {
            throw new ValidationSchemaNotFoundException(String.format(VALIDATION_SCHEMA_NOT_FOUND, registryId));
        }
        return validateEntity(registryId, entityDto, validationSchema, action);
    }

    private EntityDto validateEntity(String registryId, EntityDto entityDto, String validationSchema,
        BiFunction<String, EntityDto, EntityDto> action) throws EntityFailedShaclValidationException {
        DataValidator dataValidator = new DataValidator(parseModel(validationSchema, VALIDATION_SCHEMA_LANGUAGE));
        Model dataModel = parseModel(entityDto.getBody(), VALIDATION_SCHEMA_LANGUAGE);
        if (dataValidator.isValidEntry(dataModel)) {
            return action.apply(registryId, entityDto);
        } else {
            throw new EntityFailedShaclValidationException();
        }
    }

    private EntityDto updateEntityInRegistry(String registryId, EntityDto entityDto) {
        Entity entity = entityManager.updateEntity(registryId, EntityConverter.toEntity(entityDto));
        return EntityConverter.toEntityDto(entity);
    }

    private EntityDto addEntityToRegistry(String registryId, EntityDto entityDto) {
        Entity entity = entityManager.addEntity(registryId, EntityConverter.toEntity(entityDto));
        return EntityConverter.toEntityDto(entity);
    }
}
