package no.bibsys.db;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.util.Optional;
import no.bibsys.db.structures.EntityRegistryTemplate;
import no.bibsys.testtemplates.LocalDynamoTest;
import no.bibsys.testtemplates.SampleData.Entry;
import org.junit.Test;

import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;

public class EntityManagerTest extends LocalDynamoTest {

    @Test
    public void addEntity_RegistryExist_ReturnsTrue() throws IOException {

        String tableName = "addEntity";
        EntityRegistryTemplate testTemplate = new EntityRegistryTemplate(tableName);
        registryManager.createRegistryFromTemplate(testTemplate);

        Entry entry = sampleData.sampleEntry();
        Optional<String> addEntity = entityManager.addEntity(tableName, entry.jsonString());
        assertThat(addEntity.isPresent(), equalTo(true));
        String entityId = addEntity.get();
        Optional<String> readEntity = entityManager.getEntity(tableName, entityId);
        assertThat(readEntity.isPresent(), equalTo(true));

    }

    @Test 
    public void addEntity_RegistryNotExisting_ReturnsFalse() throws IOException {
        String tableName = "addEntityNoRegistry";
        Entry entry = sampleData.sampleEntry();
        Optional<String> addEntity = entityManager.addEntity(tableName, entry.jsonString());
        assertThat(addEntity.isPresent(), equalTo(false));
    }

    @Test
    public void deleteEntity_EntityExists_ReturnsTrue() throws IOException {
        String tableName = "deleteEntity";
        EntityRegistryTemplate testTemplate = new EntityRegistryTemplate(tableName);
        registryManager.createRegistryFromTemplate(testTemplate);

        Entry entry = sampleData.sampleEntry();
        Optional<String> addEntity = entityManager.addEntity(tableName, entry.jsonString());
        assertThat(addEntity.isPresent(), equalTo(true));
        String entityId = addEntity.get();
        boolean deleteEntity = entityManager.deleteEntity(tableName, entityId);
        assertThat(deleteEntity, equalTo(true));
    }

    @Test
    public void deleteEntity_EntityNotExisting_ReturnsFalse() throws IOException {
        String tableName = "deleteEntityNoEntity";
        EntityRegistryTemplate testTemplate = new EntityRegistryTemplate(tableName);
        registryManager.createRegistryFromTemplate(testTemplate);

        String entityId = "nonExistingEntityId";
        boolean deleteEntity = entityManager.deleteEntity(tableName, entityId);
        assertThat(deleteEntity, equalTo(false));
    }

    @Test
    public void deleteEntity_RegistryNotExisting_ReturnsFalse() throws IOException {
        String tableName = "deleteEntityNoRegistry";

        String entityId = "nonExistingEntityId";
        boolean deleteEntity = entityManager.deleteEntity(tableName, entityId);
        assertThat(deleteEntity, equalTo(false));
    }

    @Test
    public void entityExists_EntityExisting_ReturnsTrue() throws IOException {
        String tableName = "entityExists";
        EntityRegistryTemplate testTemplate = new EntityRegistryTemplate(tableName);
        registryManager.createRegistryFromTemplate(testTemplate);

        Entry entry = sampleData.sampleEntry();
        Optional<String> addEntity = entityManager.addEntity(tableName, entry.jsonString());
        String entityId = addEntity.get();
        boolean entityExists = entityManager.entityExists(tableName, entityId);
        assertThat(entityExists, equalTo(true));
    }

    @Test
    public void entityExists_EntityNotExisting_ReturnsFalse() throws IOException {
        String tableName = "entityExistsNoEntity";
        EntityRegistryTemplate testTemplate = new EntityRegistryTemplate(tableName);
        registryManager.createRegistryFromTemplate(testTemplate);

        String entityId = "nonExistingEntity";
        boolean entityExists = entityManager.entityExists(tableName, entityId);
        assertThat(entityExists, equalTo(false));
    }

    @Test
    public void entityExists_RegistryNotExisting_ReturnsFalse() throws IOException {
        String tableName = "entityExistsNoRegistry";

        String entityId = "nonExistingEntity";
        boolean entityExists = entityManager.entityExists(tableName, entityId);
        assertThat(entityExists, equalTo(false));
    }
    
    @Test
    public void getEntity_EntityExisting_ReturnsTrue() throws IOException {
        String tableName = "getEntity";
        EntityRegistryTemplate testTemplate = new EntityRegistryTemplate(tableName);
        registryManager.createRegistryFromTemplate(testTemplate);

        Entry entry = sampleData.sampleEntry();
        Optional<String> addEntity = entityManager.addEntity(tableName, entry.jsonString());
        String entityId = addEntity.get();
        Optional<String> getEntity = entityManager.getEntity(tableName, entityId);
        assertThat(getEntity.isPresent(), equalTo(true));
    }

    @Test
    public void getEntity_EntityNotExisting_ReturnsFalse() throws IOException {
        String tableName = "getEntityNoEntity";
        EntityRegistryTemplate testTemplate = new EntityRegistryTemplate(tableName);
        registryManager.createRegistryFromTemplate(testTemplate);

        String entityId = "nonExistingEntityId";
        Optional<String> getEntity = entityManager.getEntity(tableName, entityId);
        assertThat(getEntity.isPresent(), equalTo(false));
    }

    @Test
    public void getEntity_RegistryNotExisting_ReturnsFalse() throws IOException {
        String tableName = "getEntityNoRegistry";
        
        String entityId = "nonExistingEntityId";
        Optional<String> getEntity = entityManager.getEntity(tableName, entityId);
        assertThat(getEntity.isPresent(), equalTo(false));
    }

    @Test
    public void updateEntity_EntityExisting_EntityUpdated() throws IOException {
        String tableName = "updateEntity";
        EntityRegistryTemplate testTemplate = new EntityRegistryTemplate(tableName);
        registryManager.createRegistryFromTemplate(testTemplate);

        Entry entry = sampleData.sampleEntry();
        Optional<String> addEntity = entityManager.addEntity(tableName, entry.jsonString());
        String entityId = addEntity.get();

        Entry updateEntry = sampleData.sampleEntry();
        String updatedLabel = "An updated label";
        updateEntry.root.put("id", entityId);
        updateEntry.root.put("label", updatedLabel);
        Optional<String> updateEntity =
                entityManager.updateEntity(tableName, entityId, updateEntry.jsonString());
        assertThat(updateEntity.isPresent(), equalTo(true));

        Optional<String> readEntity = entityManager.getEntity(tableName, entityId);
        String readLabel =
                JsonUtils.getObjectMapper().readTree(readEntity.get()).get("label").asText();
        assertThat(readLabel, equalTo(updatedLabel));

    }

    @Test
    public void updateEntity_EntityNotExisting_ReturnsFalse() throws IOException {
        String tableName = "updateEntityNoEntity";
        EntityRegistryTemplate testTemplate = new EntityRegistryTemplate(tableName);
        registryManager.createRegistryFromTemplate(testTemplate);

        String entityId = "nonExistingEntityId";

        Entry updateEntry = sampleData.sampleEntry();
        String updatedLabel = "An updated label";
        updateEntry.root.put("id", entityId);
        updateEntry.root.put("label", updatedLabel);
        Optional<String> updateEntity =
                entityManager.updateEntity(tableName, entityId, updateEntry.jsonString());
        assertThat(updateEntity.isPresent(), equalTo(false));
    }

    @Test
    public void updateEntity_RegistryNotExisting_ReturnsFalse() {
        String tableName = "updateEntityNoRegistry";

        String entityId = "nonExistingEntityId";

        Entry updateEntry = sampleData.sampleEntry();
        String updatedLabel = "An updated label";
        updateEntry.root.put("id", entityId);
        updateEntry.root.put("label", updatedLabel);
        Optional<String> updateEntity =
                entityManager.updateEntity(tableName, entityId, updateEntry.jsonString());
        assertThat(updateEntity.isPresent(), equalTo(false));
    }
}
