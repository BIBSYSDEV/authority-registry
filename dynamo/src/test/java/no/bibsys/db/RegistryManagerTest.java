package no.bibsys.db;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import no.bibsys.db.exceptions.RegistryAlreadyExistsException;
import no.bibsys.db.structures.Entity;
import no.bibsys.db.structures.Registry;


public class RegistryManagerTest extends LocalDynamoTest {


    @Test
    public void createRegistry_RegistryNotExisting_RegistryExists() throws Exception {

        String registryName = "createARegistry";
        boolean existsBeforeCreation = registryManager.registryExists(registryMetadataTableName, registryName);

        Registry registry = sampleData.sampleRegistry(registryName);

        registryManager.createRegistry(registryMetadataTableName, registry);
        boolean existsAfterCreation = registryManager.registryExists(registryMetadataTableName, registryName);
        assertFalse(existsBeforeCreation);
        assertTrue(existsAfterCreation);
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

    @Test(expected = RegistryAlreadyExistsException.class)
    public void createRegistry_RegistryAlreadyExists_ThrowsException()
            throws Exception {

        String registryName = "tableAlreadyExists";
        boolean existsBeforeCreation = registryManager.registryExists(registryMetadataTableName, registryName);
        assertThat("The table should not exist before creation", existsBeforeCreation,
                is(equalTo(false)));
        Registry registry = sampleData.sampleRegistry(registryName);
        registryManager.createRegistry(registryMetadataTableName, registry);
        boolean existsAfterCreation = registryManager.registryExists(registryMetadataTableName, registryName);
        assertThat("The table should  exist before creation", existsAfterCreation,
                is(equalTo(true)));

        registryManager.createRegistry(registryMetadataTableName, registry);
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

    @Test
    public void createRegistryFromTemplate_RegistryDoesNotExist_RegistryExists()
            throws Exception {
        String registryName = "addSchemaToRegistry";
        Registry registry = sampleData.sampleRegistry(registryName);

        registryManager.createRegistry(registryMetadataTableName, registry);
        String schemaAsJson = "JSON validation schema";
        registry.setSchema(schemaAsJson);
        registryManager.updateRegistryMetadata(registryMetadataTableName, registry);

        assertThat(registryManager.getRegistry(registryMetadataTableName, registryName), is(equalTo(registry)));
    }
}
