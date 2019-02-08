package no.bibsys.db;

import no.bibsys.db.exceptions.RegistryAlreadyExistsException;
import no.bibsys.db.exceptions.RegistryCreationFailureException;
import no.bibsys.db.exceptions.RegistryMetadataTableBeingCreatedException;
import no.bibsys.db.exceptions.RegistryNotFoundException;
import no.bibsys.db.exceptions.SettingValidationSchemaUponCreationException;
import no.bibsys.db.structures.Entity;
import no.bibsys.db.structures.Registry;
import no.bibsys.db.structures.RegistryStatus;
import no.bibsys.entitydata.validation.exceptions.ShaclModelValidationException;
import no.bibsys.entitydata.validation.exceptions.TargetClassPropertyObjectIsNotAResourceException;
import no.bibsys.utils.IoUtils;
import no.bibsys.utils.ModelParser;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class RegistryManagerTest extends LocalDynamoTest {

    private static final String VALID_VALIDATION_SCHEMA_JSON = "validShaclValidationSchema.json";
    private static final String VALIDATION_FOLDER = "validation";
    private static final String INVALID_SHACL_VALIDATION_SCHEMA_JSON = "invalidDatatypeRangeShaclValidationSchema.json";
    private static final String ALT_VALID_SHACL_VALIDATION_SCHEMA_JSON = "alternativeValidShaclValidationSchema.json";

    private ModelParser modelParser = new ModelParser();

    @Test
    public void getRegistry_regsitryExists_registy()
            throws SettingValidationSchemaUponCreationException, RegistryCreationFailureException,
            RegistryMetadataTableBeingCreatedException {
        Registry expectedRegistry = sampleData.sampleRegistry("getRegistry");
        registryManager.createRegistry(registryMetadataTableName, expectedRegistry);
        Registry actualRegistry = registryManager.getRegistry(registryMetadataTableName, expectedRegistry.getId());

        assertThat(expectedRegistry, is(equalTo(actualRegistry)));
    }

    @Test(expected = RegistryNotFoundException.class)
    public void getRegistry_regsitryNotExists_exception()
            throws SettingValidationSchemaUponCreationException, RegistryCreationFailureException,
            RegistryMetadataTableBeingCreatedException {
        Registry registryForMetadataTableCreation = sampleData.sampleRegistry("getRegistry");
        registryManager.createRegistry(registryMetadataTableName, registryForMetadataTableCreation);
        Registry notExistingRegistry = sampleData.sampleRegistry("anotherRegistry");
        Registry actualRegistry = registryManager.getRegistry(registryMetadataTableName, notExistingRegistry.getId());

        assertThat(actualRegistry, is(not(equalTo(actualRegistry))));
    }

    @Test
    public void getRegistries_someRegistries_registryList()
            throws SettingValidationSchemaUponCreationException, RegistryCreationFailureException,
            RegistryMetadataTableBeingCreatedException {
        Set<String> registryNames = new HashSet<>(Arrays.asList("registry1", "registry2"));
        for (String regName : registryNames) {
            registryManager.createRegistry(registryMetadataTableName, sampleData.sampleRegistry(regName));
        }

        Set<String> actualList = new HashSet<>(registryManager.getRegistries(registryMetadataTableName));
        assertThat(actualList, is(equalTo(registryNames)));

    }

    @Test
    public void createRegistry_RegistryNotExisting_RegistryExists() throws Exception {

        String registryName = "createARegistry";
        boolean existsBeforeCreation = registryManager.status(registryName).equals(RegistryStatus.ACTIVE);

        Registry registry = sampleData.sampleRegistry(registryName);

        registryManager.createRegistry(registryMetadataTableName, registry);
        boolean existsAfterCreation = registryManager.status(registryName).equals(RegistryStatus.ACTIVE);
        assertFalse(existsBeforeCreation);
        assertTrue(existsAfterCreation);
    }

    @Test
    public void createRegistryFromTemplate_RegistryDoesNotExist_RegistryExists() throws Exception {
        String registryName = "addSchemaToRegistry";
        Registry registry = sampleData.sampleRegistry(registryName);

        registryManager.createRegistry(registryMetadataTableName, registry);
        String schemaAsJson = "JSON validation schema";
        registry.setSchema(schemaAsJson);
        registryManager.updateRegistryMetadata(registryMetadataTableName, registry);

        assertThat(registryManager.getRegistry(registryMetadataTableName, registryName), is(equalTo(registry)));
    }

    @Test(expected = RegistryAlreadyExistsException.class)
    public void createRegistry_RegistryAlreadyExists_ThrowsException() throws Exception {

        String registryName = "tableAlreadyExists";
        boolean existsBeforeCreation = registryManager.status(registryName).equals(RegistryStatus.ACTIVE);
        assertThat("The table should not exist before creation", existsBeforeCreation, is(equalTo(false)));
        Registry registry = sampleData.sampleRegistry(registryName);
        registryManager.createRegistry(registryMetadataTableName, registry);
        boolean existsAfterCreation = registryManager.status(registryName).equals(RegistryStatus.ACTIVE);
        assertThat("The table should  exist before creation", existsAfterCreation, is(equalTo(true)));

        registryManager.createRegistry(registryMetadataTableName, registry);
    }

    @Test(expected = RegistryCreationFailureException.class)
    public void createRegistry_registryNotExistsRegistryNotCreated_exception()
            throws IOException, SettingValidationSchemaUponCreationException,
            RegistryMetadataTableBeingCreatedException, RegistryCreationFailureException {
        String registryName = "aRegistry";
        Registry registry = sampleData.sampleRegistry(registryName);
        RegistryManager registryManager = registryManagerThatFailsToCreateATable();
        registryManager.createRegistry(registryMetadataTableName, registry);
    }

    @Test(expected = RegistryMetadataTableBeingCreatedException.class)
    public void createRegistry_registryNotExistsMetadataTableIsBeingCreated_exception()
            throws IOException, SettingValidationSchemaUponCreationException,
            RegistryMetadataTableBeingCreatedException, RegistryCreationFailureException {
        String registryName = "aRegistry";
        Registry registry = sampleData.sampleRegistry(registryName);
        RegistryManager registryManager = registryManagerThatIsCreatingMetadataTable();
        registryManager.createRegistry(registryMetadataTableName, registry);
    }

    @Test(expected = RegistryMetadataTableBeingCreatedException.class)
    public void createRegistry_registryNotExistsMetadataTableNotFound_exception()
            throws IOException, SettingValidationSchemaUponCreationException,
            RegistryMetadataTableBeingCreatedException, RegistryCreationFailureException {
        String registryName = "aRegistry";
        Registry registry = sampleData.sampleRegistry(registryName);
        RegistryManager registryManager = registryManagerThatFailsCreatingMetadataTable();
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

    @Test(expected = SettingValidationSchemaUponCreationException.class)
    public void createRegistry_RegistryNotExistsInValidJsonDocument_cannotSetSchemaUponCreationException()
            throws RegistryMetadataTableBeingCreatedException, SettingValidationSchemaUponCreationException,
            RegistryCreationFailureException {
        String registryName = "aRegistry";
        Registry registry = sampleData.sampleRegistry(registryName);

        registry.setSchema("InvalidInput");
        registryManager.createRegistry(registryMetadataTableName, registry);

    }

    @Test
    public void createRegistry_RegistryNotExistsValidShema_registryWithValidSchema()
            throws IOException, RegistryMetadataTableBeingCreatedException,
            SettingValidationSchemaUponCreationException, ShaclModelValidationException,
            TargetClassPropertyObjectIsNotAResourceException, RegistryCreationFailureException {
        String validationSchemaStr =
                IoUtils.resourceAsString(Paths.get(VALIDATION_FOLDER, VALID_VALIDATION_SCHEMA_JSON));
        Registry createdRegistry = createRegistry();
        createdRegistry = updateRegistryWithValidSchema(createdRegistry);

        Model expectedValidationSchema = modelParser.parseModel(validationSchemaStr, Lang.JSONLD);
        Model actualValidationSchema = modelParser.parseModel(createdRegistry.getSchema(), Lang.JSONLD);

        assertTrue(expectedValidationSchema.isIsomorphicWith(actualValidationSchema));
    }

    private Registry createRegistry()
            throws RegistryMetadataTableBeingCreatedException, SettingValidationSchemaUponCreationException,
            RegistryCreationFailureException {
        String registryName = "aRegistry";
        Registry registry = sampleData.sampleRegistry(registryName);
        return registryManager.createRegistry(registryMetadataTableName, registry);

    }

    @Test(expected = ShaclModelValidationException.class)
    public void createRegistry_RegistryNotExistingInValidSchema_invalidSchemaException()
            throws IOException, RegistryMetadataTableBeingCreatedException,
            SettingValidationSchemaUponCreationException, ShaclModelValidationException,
            TargetClassPropertyObjectIsNotAResourceException, RegistryCreationFailureException {

        Registry registry = createRegistry();
        updateRegistryWithInvalidSchema(registry);
    }

    @Test
    public void updateRegistry_RegistryExistsValidShema_registryWithValidSchema()
            throws IOException, RegistryMetadataTableBeingCreatedException, ShaclModelValidationException,
            SettingValidationSchemaUponCreationException, TargetClassPropertyObjectIsNotAResourceException,
            RegistryCreationFailureException {

        Registry createdRegistry = createRegistry();
        createdRegistry = updateRegistryWithValidSchema(createdRegistry);
        String alternativeValidationSchema =
                IoUtils.resourceAsString(Paths.get(VALIDATION_FOLDER, ALT_VALID_SHACL_VALIDATION_SCHEMA_JSON));
        createdRegistry = registryManager
                .updateRegistrySchema(registryMetadataTableName, createdRegistry.getId(), alternativeValidationSchema);

        Model actualValidationSchema = modelParser.parseModel(createdRegistry.getSchema(), Lang.JSONLD);
        Model expectedValidationSchema = modelParser.parseModel(alternativeValidationSchema, Lang.JSONLD);
        assertTrue(expectedValidationSchema.isIsomorphicWith(actualValidationSchema));
    }

    @Test(expected = ShaclModelValidationException.class)
    public void updateRegistry_RegistryExistsInValidShema_registryWithValidSchema()
            throws IOException, RegistryMetadataTableBeingCreatedException, ShaclModelValidationException,
            SettingValidationSchemaUponCreationException, TargetClassPropertyObjectIsNotAResourceException,
            RegistryCreationFailureException {

        Registry createdRegistry = createRegistry();
        createdRegistry = updateRegistryWithValidSchema(createdRegistry);
        String alternativeValidationSchema =
                IoUtils.resourceAsString(Paths.get(VALIDATION_FOLDER, INVALID_SHACL_VALIDATION_SCHEMA_JSON));
        registryManager
                .updateRegistrySchema(registryMetadataTableName, createdRegistry.getId(), alternativeValidationSchema);

    }

    @Test
    public void deleteRegistry_RegistryExists_registryDeleted()
            throws SettingValidationSchemaUponCreationException, RegistryMetadataTableBeingCreatedException,
            TargetClassPropertyObjectIsNotAResourceException, IOException, ShaclModelValidationException,
            RegistryCreationFailureException {
        Registry registry = createRegistry();
        registry = updateRegistryWithValidSchema(registry);
        registryManager.deleteRegistry(registryMetadataTableName, registry.getId());

    }

    private Registry updateRegistryWithValidSchema(Registry registry)
            throws IOException, ShaclModelValidationException, TargetClassPropertyObjectIsNotAResourceException {
        return registryManager.updateRegistrySchema(registryMetadataTableName, registry.getId(),
                sampleData.getValidValidationSchemaString());
    }

    private void updateRegistryWithInvalidSchema(Registry registry)
            throws IOException, ShaclModelValidationException, TargetClassPropertyObjectIsNotAResourceException {
        registryManager.updateRegistrySchema(registryMetadataTableName, registry.getId(),
                sampleData.getInvalidValidationSchemaString());
    }

}
