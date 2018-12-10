package no.bibsys.db;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import org.junit.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import no.bibsys.db.exceptions.RegistryAlreadyExistsException;
import no.bibsys.db.structures.Entity;
import no.bibsys.db.structures.Registry;


public class RegistryManagerTest extends LocalDynamoTest {


    @Test
    public void createRegistry_RegistryNotExisting_RegistryExists() throws IOException {

        String registryName = "createARegistry";
        boolean existsBeforeCreation = registryManager.registryExists(validationSchemaTableName, registryName);

        Registry registry = sampleData.sampleRegistry(registryName);

        registryManager.createRegistry(validationSchemaTableName, registry);
        boolean existsAfterCreation = registryManager.registryExists(validationSchemaTableName, registryName);
        assertFalse(existsBeforeCreation);
        assertTrue(existsAfterCreation);
    }

    @Test
    public void updateMetadata_RegistryExisting_MetadataUpdated() throws IOException {

        String registryName = "addMetadataRegistry";

        Registry registry = sampleData.sampleRegistry(registryName);
        registryManager.createRegistry(validationSchemaTableName, registry);
        registryManager.updateRegistryMetadata(validationSchemaTableName, registry);
        Registry metadata = registryManager.getRegistry(validationSchemaTableName, registryName);

        assertThat(metadata.getId(), is(equalTo(registryName)));

    }

    @Test
    public void updateMetadata_NonEmptyRegistryExisting_MetadataUpdated() throws IOException {
        
        String registryName = "updateNonEmptyMetadataRegistry";
        
        Registry registry = sampleData.sampleRegistry(registryName);
        registryManager.createRegistry(validationSchemaTableName, registry); 

        assertThat(registry.getMetadata().get("label").asText(), is(equalTo("label")));
        
        Entity entity = sampleData.sampleEntity();
        entityManager.addEntity(registryName, entity);
        
        String updatedLabel = "Updated label";
        
        registry.getMetadata().put("label", updatedLabel);
        
        registryManager.updateRegistryMetadata(validationSchemaTableName, registry);
        Registry metadata = registryManager.getRegistry(validationSchemaTableName, registryName);

        assertThat(metadata.getId(), is(equalTo(registryName)));
        assertThat(registry.getMetadata().get("label").asText(), is(equalTo(updatedLabel)));
        
    }

    @Test(expected = RegistryAlreadyExistsException.class)
    public void createRegistry_RegistryAlreadyExists_ThrowsException()
            throws JsonProcessingException {

        String registryName = "tableAlreadyExists";
        boolean existsBeforeCreation = registryManager.registryExists(validationSchemaTableName, registryName);
        assertThat("The table should not exist before creation", existsBeforeCreation,
                is(equalTo(false)));
        Registry registry = sampleData.sampleRegistry(registryName);
        registryManager.createRegistry(validationSchemaTableName, registry);
        boolean existsAfterCreation = registryManager.registryExists(validationSchemaTableName, registryName);
        assertThat("The table should  exist before creation", existsAfterCreation,
                is(equalTo(true)));

        registryManager.createRegistry(validationSchemaTableName, registry);
    }

    @Test
    public void emptyRegistry_RegistryExists_RegistryIsEmpty() throws IOException {

        String registryName = "emptyRegistry";
        Registry registry = sampleData.sampleRegistry(registryName);
        registryManager.createRegistry(validationSchemaTableName, registry);
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
            throws IOException, InterruptedException {
        String registryName = "addSchemaToRegistry";
        Registry registry = sampleData.sampleRegistry(registryName);

        registryManager.createRegistry(validationSchemaTableName, registry);
        String schemaAsJson = "JSON validation schema";
        registry.setSchema(schemaAsJson);
        registryManager.updateRegistryMetadata(validationSchemaTableName, registry);

        assertThat(registryManager.getRegistry(validationSchemaTableName, registryName), is(equalTo(registry)));
    }
}
