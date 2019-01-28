package no.bibsys.db;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import no.bibsys.db.exceptions.RegistryAlreadyExistsException;
import no.bibsys.db.exceptions.RegistryMetadataTableBeingCreatedException;
import no.bibsys.db.structures.Entity;
import no.bibsys.db.structures.Registry;
import no.bibsys.entitydata.validation.ModelParser;
import no.bibsys.entitydata.validation.exceptions.ShaclModelValidationException;
import no.bibsys.entitydata.validation.exceptions.ValidationSchemaSyntaxErrorException;
import no.bibsys.utils.IoUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.junit.Test;


public class RegistryManagerTest extends LocalDynamoTest {


    public static final String VALID_VALIDATION_SCHEMA_JSON = "validShaclValidationSchema.json";
    public static final String VALIDATION_FOLDER = "validation";
    public static final String INVALID_SHACL_VALIDATION_SCHEMA_JSON =
        "invalidDatatypeRangeShaclValidationSchema.json";
    public static final String ALT_VALID_SHACL_VALIDATION_SCHEMA_JSON =
        "alternativeValidShaclValidationSchema.json";
    private ModelParser modelParser = new ModelParser();

    @Test
    public void createRegistry_RegistryNotExisting_RegistryExists() throws Exception {

        String registryName = "createARegistry";
        boolean existsBeforeCreation = registryManager
            .registryExists(registryMetadataTableName, registryName);

        Registry registry = sampleData.sampleRegistry(registryName);

        registryManager.createRegistry(registryMetadataTableName, registry);
        boolean existsAfterCreation = registryManager
            .registryExists(registryMetadataTableName, registryName);
        assertFalse(existsBeforeCreation);
        assertTrue(existsAfterCreation);
    }


    // TODO: check if this test is the same as the previous one
    @Test
    public void createRegistryFromTemplate_RegistryDoesNotExist_RegistryExists() throws Exception {
        String registryName = "addSchemaToRegistry";
        Registry registry = sampleData.sampleRegistry(registryName);

        registryManager.createRegistry(registryMetadataTableName, registry);
        String schemaAsJson = "JSON validation schema";
        registry.setSchema(schemaAsJson);
        registryManager.updateRegistryMetadata(registryMetadataTableName, registry);

        assertThat(registryManager.getRegistry(registryMetadataTableName, registryName),
            is(equalTo(registry)));
    }


    @Test(expected = RegistryAlreadyExistsException.class)
    public void createRegistry_RegistryAlreadyExists_ThrowsException() throws Exception {

        String registryName = "tableAlreadyExists";
        boolean existsBeforeCreation = registryManager
            .registryExists(registryMetadataTableName, registryName);
        assertThat("The table should not exist before creation", existsBeforeCreation,
            is(equalTo(false)));
        Registry registry = sampleData.sampleRegistry(registryName);
        registryManager.createRegistry(registryMetadataTableName, registry);
        boolean existsAfterCreation = registryManager
            .registryExists(registryMetadataTableName, registryName);
        assertThat("The table should  exist before creation", existsAfterCreation,
            is(equalTo(true)));

        registryManager.createRegistry(registryMetadataTableName, registry);
    }


    @Test
    public void updateMetadata_RegistryExisting_MetadataUpdated() throws Exception {

        String registryName = "addMetadataRegistry";

        Registry registry = sampleData.sampleRegistry(registryName);
        registryManager.createRegistry(registryMetadataTableName, registry);
        registryManager.updateRegistryMetadata(registryMetadataTableName, registry);
        Registry metadata = registryManager.getRegistry(registryMetadataTableName, registryName);

        assertThat(metadata.getId(), is(equalTo(registryName)));

    }

    @Test
    public void updateMetadata_NonEmptyRegistryExisting_MetadataUpdated() throws Exception {

        String registryName = "updateNonEmptyMetadataRegistry";

        Registry registry = sampleData.sampleRegistry(registryName);
        registryManager.createRegistry(registryMetadataTableName, registry);

        assertThat(registry.getMetadata().get("label").asText(), is(equalTo("label")));

        Entity entity = sampleData.sampleEntity();
        entityManager.addEntity(registryName, entity);

        String updatedLabel = "Updated label";

        registry.getMetadata().put("label", updatedLabel);

        registryManager.updateRegistryMetadata(registryMetadataTableName, registry);
        Registry metadata = registryManager.getRegistry(registryMetadataTableName, registryName);

        assertThat(metadata.getId(), is(equalTo(registryName)));
        assertThat(registry.getMetadata().get("label").asText(), is(equalTo(updatedLabel)));

    }


    @Test
    public void emptyRegistry_RegistryExists_RegistryIsEmpty() throws Exception {

        String registryName = "emptyRegistry";
        Registry registry = sampleData.sampleRegistry(registryName);
        registryManager.createRegistry(registryMetadataTableName, registry);
        Entity entity = sampleData.sampleEntity();
        Entity addEntity = entityManager.addEntity(registryName, entity);
        boolean entityExists = entityManager.entityExists(registryName, addEntity.getId());
        assertThat(entityExists, equalTo(true));

        registryManager.emptyRegistry(registryName);
        boolean entityExistAfterEmpty = entityManager.entityExists(registryName, addEntity.getId());
        assertThat(entityExistAfterEmpty, equalTo(false));
    }


    @Test(expected = ValidationSchemaSyntaxErrorException.class)
    public void createRegistry_RegistryNotExistsInValidJsonDocument_invalidSchemaException()
        throws IOException, RegistryMetadataTableBeingCreatedException,
        ShaclModelValidationException {
        String registryName = "aRegistry";
        Registry registry = sampleData.sampleRegistry(registryName);

        registry.setSchema("InvalidInput");
        registryManager.createRegistry(registryMetadataTableName, registry);

    }


    @Test
    public void createRegistry_RegistryNotExistsValidShema_registryWithValidSchema()
        throws IOException, RegistryMetadataTableBeingCreatedException,
        ShaclModelValidationException {
        String validationSchemaStr = IoUtils
            .resourceAsString(Paths.get(VALIDATION_FOLDER, VALID_VALIDATION_SCHEMA_JSON));
        Registry createdRegistry = createRegistryWithValidationSchema(validationSchemaStr);

        Model expectedValidationSchema = modelParser.parseModel(validationSchemaStr, Lang.JSONLD);
        Model actualValidationSchema = modelParser
            .parseModel(createdRegistry.getSchema(), Lang.JSONLD);

        assertTrue(expectedValidationSchema.isIsomorphicWith(actualValidationSchema));
    }

    private Registry createRegistryWithValidationSchema(String validationSchemaStr)
        throws IOException, ShaclModelValidationException,
        RegistryMetadataTableBeingCreatedException {
        String registryName = "aRegistry";
        Registry registry = sampleData.sampleRegistry(registryName);
        registry.setSchema(validationSchemaStr);
        return registryManager.createRegistry(registryMetadataTableName, registry);

    }

    @Test(expected = ShaclModelValidationException.class)
    public void createRegistry_RegistryNotExistingInValidSchema_exception()
        throws IOException, RegistryMetadataTableBeingCreatedException,
        ShaclModelValidationException {
        String validationSchemaStr = IoUtils
            .resourceAsString(Paths.get(VALIDATION_FOLDER, INVALID_SHACL_VALIDATION_SCHEMA_JSON));
        createRegistryWithValidationSchema(validationSchemaStr);
    }

    @Test
    public void updateRegistry_RegistryExistsValidShema_registryWithValidSchema()
        throws IOException, RegistryMetadataTableBeingCreatedException,
        ShaclModelValidationException {

        String validationSchemaStr = IoUtils
            .resourceAsString(Paths.get(VALIDATION_FOLDER, VALID_VALIDATION_SCHEMA_JSON));

        Registry createdRegistry = createRegistryWithValidationSchema(validationSchemaStr);

        String alternativeValidationSchema = IoUtils
            .resourceAsString(Paths.get(VALIDATION_FOLDER, ALT_VALID_SHACL_VALIDATION_SCHEMA_JSON));
        createdRegistry = registryManager
            .updateRegistrySchema(registryMetadataTableName, createdRegistry.getId(),
                alternativeValidationSchema);

        Model actualValidationSchema = modelParser
            .parseModel(createdRegistry.getSchema(), Lang.JSONLD);
        Model expectedValidationSchema = modelParser
            .parseModel(alternativeValidationSchema, Lang.JSONLD);
        assertTrue(expectedValidationSchema.isIsomorphicWith(actualValidationSchema));
    }

    @Test(expected = ShaclModelValidationException.class)
    public void updateRegistry_RegistryExistsInValidShema_registryWithValidSchema()
        throws IOException, RegistryMetadataTableBeingCreatedException,
        ShaclModelValidationException {

        String validationSchemaStr = IoUtils
            .resourceAsString(Paths.get(VALIDATION_FOLDER, VALID_VALIDATION_SCHEMA_JSON));

        Registry createdRegistry = createRegistryWithValidationSchema(validationSchemaStr);

        String alternativeValidationSchema = IoUtils
            .resourceAsString(Paths.get(VALIDATION_FOLDER, INVALID_SHACL_VALIDATION_SCHEMA_JSON));
        createdRegistry = registryManager
            .updateRegistrySchema(registryMetadataTableName, createdRegistry.getId(),
                alternativeValidationSchema);

    }


}
