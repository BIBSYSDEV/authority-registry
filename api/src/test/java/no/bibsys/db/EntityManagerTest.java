package no.bibsys.db;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import org.junit.Test;
import no.bibsys.db.structures.Entity;
import no.bibsys.db.structures.EntityRegistryTemplate;
import no.bibsys.testtemplates.LocalDynamoTest;
import no.bibsys.web.exception.EntityNotFoundException;
import no.bibsys.web.exception.RegistryNotFoundException;

public class EntityManagerTest extends LocalDynamoTest {

    @Test
    public void addEntity_RegistryExist_ReturnsEntity() throws Exception {

        String tableName = "addEntity";
        EntityRegistryTemplate testTemplate = new EntityRegistryTemplate(tableName);
        registryManager.createRegistryFromTemplate(testTemplate);

        Entity entity = sampleData.sampleEntity();
        Entity addEntity = entityManager.addEntity(tableName, entity.getBodyAsJson());
        assertEquals(entity, addEntity);
    }

    @Test(expected = RegistryNotFoundException.class)
    public void addEntity_RegistryNotExisting_ThrowsException() throws IOException {
        String tableName = "addEntityNoRegistry";
        Entity entity = sampleData.sampleEntity();
        entityManager.addEntity(tableName, entity.getBodyAsJson());
    }

    @Test
    public void deleteEntity_EntityExists_ReturnsTrue() throws IOException {
        String tableName = "deleteEntity";
        EntityRegistryTemplate testTemplate = new EntityRegistryTemplate(tableName);
        registryManager.createRegistryFromTemplate(testTemplate);

        Entity entity = sampleData.sampleEntity();

        Entity addEntity = entityManager.addEntity(tableName, entity.getBodyAsJson());
        assertEquals(entity, addEntity);
        boolean deleteEntity = entityManager.deleteEntity(tableName, addEntity.getId());
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

        Entity entity = sampleData.sampleEntity();

        Entity addEntity = entityManager.addEntity(tableName, entity.getBodyAsJson());
        boolean entityExists = entityManager.entityExists(tableName, addEntity.getId());
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

        Entity entity = sampleData.sampleEntity();

        Entity addEntity = entityManager.addEntity(tableName, entity.getBodyAsJson());
        Entity getEntity = entityManager.getEntity(tableName, addEntity.getId());
        assertEquals(entity, getEntity);
    }

    @Test(expected = EntityNotFoundException.class)
    public void getEntity_EntityNotExisting_ReturnsFalse() throws IOException {
        String tableName = "getEntityNoEntity";
        EntityRegistryTemplate testTemplate = new EntityRegistryTemplate(tableName);
        registryManager.createRegistryFromTemplate(testTemplate);

        String entityId = "nonExistingEntityId";
        entityManager.getEntity(tableName, entityId);
    }

    @Test(expected = RegistryNotFoundException.class)
    public void getEntity_RegistryNotExisting_ThrowsException() throws IOException {
        String tableName = "getEntityNoRegistry";

        String entityId = "nonExistingEntityId";
        entityManager.getEntity(tableName, entityId);
    }

    @Test
    public void updateEntity_EntityExisting_EntityUpdated() throws IOException {
        String tableName = "updateEntity";
        EntityRegistryTemplate testTemplate = new EntityRegistryTemplate(tableName);
        registryManager.createRegistryFromTemplate(testTemplate);

        Entity entity = sampleData.sampleEntity();

        Entity addEntity = entityManager.addEntity(tableName, entity.getBodyAsJson());

        String updatedLabel = "An updated label";
        addEntity.getBody().put("label", updatedLabel);

        Entity updateEntity =
                entityManager.updateEntity(tableName, addEntity.getId(), addEntity.getBodyAsJson());
        assertEquals(addEntity, updateEntity);

        Entity readEntity = entityManager.getEntity(tableName, updateEntity.getId());
        String readLabel = readEntity.getBody().get("label").asText();
        assertThat(readLabel, equalTo(updatedLabel));

    }

    @Test(expected = EntityNotFoundException.class)
    public void updateEntity_EntityNotExisting_ThrowsException() throws IOException {
        String tableName = "updateEntityNoEntity";
        EntityRegistryTemplate testTemplate = new EntityRegistryTemplate(tableName);
        registryManager.createRegistryFromTemplate(testTemplate);

        String entityId = "nonExistingEntityId";

        Entity entity = sampleData.sampleEntity();
        String updatedLabel = "An updated label";
        entity.getBody().put("label", updatedLabel);

        entityManager.updateEntity(tableName, entityId, entity.getBodyAsJson());
    }

    @Test(expected = RegistryNotFoundException.class)
    public void updateEntity_RegistryNotExisting_ThrowsException() throws IOException {
        String tableName = "updateEntityNoRegistry";

        String entityId = "nonExistingEntityId";

        Entity entity = sampleData.sampleEntity();
        String updatedLabel = "An updated label";
        entity.getBody().put("label", updatedLabel);

        entityManager.updateEntity(tableName, entityId, entity.getBodyAsJson());
    }
}
