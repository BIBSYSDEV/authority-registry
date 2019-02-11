package no.bibsys.db;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;

import no.bibsys.db.exceptions.EntityNotFoundException;
import no.bibsys.db.exceptions.RegistryMetadataTableBeingCreatedException;
import no.bibsys.db.exceptions.RegistryNotFoundException;
import no.bibsys.db.structures.Entity;
import no.bibsys.db.structures.Registry;
import no.bibsys.entitydata.validation.exceptions.ShaclModelValidationException;
import no.bibsys.entitydata.validation.exceptions.TargetClassPropertyObjectIsNotAResourceException;

public class EntityManagerTest extends LocalDynamoTest {

    @Test
    public void addEntity_RegistryExist_ReturnsEntity() throws Exception {

        String tableName = "addEntity";
        Registry registry = sampleData.sampleRegistry(tableName);
        registryManager.createRegistry(registryMetadataTableName, registry);

        Entity entity = sampleData.sampleEntity();
        Entity addEntity = entityManager.addEntity(tableName, entity);
        assertEquals(entity, addEntity);
    }

    @Test(expected = RegistryNotFoundException.class)
    public void addEntity_RegistryNotExisting_ThrowsException() throws IOException {
        String tableName = "addEntityNoRegistry";
        Entity entity = sampleData.sampleEntity();
        entityManager.addEntity(tableName, entity);
    }

    @Test(expected = RegistryMetadataTableBeingCreatedException.class)
    public void addEntity_RegistryBeingCreated_ThrowsException() throws Exception {
        Registry registry = sampleData.sampleRegistry(tableName);
        registryManager.createRegistry(registryMetadataTableName, registry);

        String tableName = "addEntityRegistryBeingCreated";
        Entity entity = sampleData.sampleEntity();
        entityManager.addEntity(tableName, entity);
    }
    
    @Test
    public void deleteEntity_EntityExists_ReturnsTrue() throws Exception {
        String tableName = "deleteEntity";
        Registry registry = sampleData.sampleRegistry(tableName);
        registryManager.createRegistry(registryMetadataTableName, registry);

        Entity entity = sampleData.sampleEntity();

        Entity addEntity = entityManager.addEntity(tableName, entity);
        assertEquals(entity, addEntity);
        boolean deleteEntity = entityManager.deleteEntity(tableName, addEntity.getId());
        assertThat(deleteEntity, equalTo(true));
    }

    @Test(expected = EntityNotFoundException.class)
    public void deleteEntity_EntityNotExisting_ThrowsException() throws Exception {
        String tableName = "deleteEntityNoEntity";
        Registry registry = sampleData.sampleRegistry(tableName);
        registryManager.createRegistry(registryMetadataTableName, registry);

        String entityId = "nonExistingEntityId";
        entityManager.deleteEntity(tableName, entityId);
    }

    @Test(expected = RegistryNotFoundException.class)
    public void deleteEntity_RegistryNotExisting_ThrowsException() throws Exception {
        String tableName = "deleteEntityNoRegistry";

        String entityId = "nonExistingEntityId";
        entityManager.deleteEntity(tableName, entityId);
    }

    @Test
    public void entityExists_EntityExisting_ReturnsTrue() throws Exception {
        String tableName = "entityExists";
        Registry registry = sampleData.sampleRegistry(tableName);
        registryManager.createRegistry(registryMetadataTableName, registry);
        updateRegistrySchema(registry);
        Entity entity = sampleData.sampleEntity();

        Entity addEntity = entityManager.addEntity(tableName, entity);
        boolean entityExists = entityManager.entityExists(tableName, addEntity.getId());
        assertThat(entityExists, equalTo(true));
    }


    @Test
    public void entityExists_EntityNotExisting_ReturnsFalse() throws Exception {
        String tableName = "entityExistsNoEntity";
        Registry registry = sampleData.sampleRegistry(tableName);
        registryManager.createRegistry(registryMetadataTableName, registry);

        String entityId = "nonExistingEntity";
        boolean entityExists = entityManager.entityExists(tableName, entityId);
        assertThat(entityExists, equalTo(false));
    }

    @Test
    public void entityExists_RegistryNotExisting_ReturnsFalse() throws Exception {
        String tableName = "entityExistsNoRegistry";

        String entityId = "nonExistingEntity";
        boolean entityExists = entityManager.entityExists(tableName, entityId);
        assertThat(entityExists, equalTo(false));
    }

    @Test
    public void getEntity_EntityExisting_ReturnsTrue() throws Exception {
        String tableName = "getEntity";
        Registry registry = sampleData.sampleRegistry(tableName);
        registryManager.createRegistry(registryMetadataTableName, registry);

        Entity entity = sampleData.sampleEntity();

        Entity addEntity = entityManager.addEntity(tableName, entity);
        Entity getEntity = entityManager.getEntity(tableName, addEntity.getId());
        assertEquals(entity, getEntity);
    }

    @Test(expected = EntityNotFoundException.class)
    public void getEntity_EntityNotExisting_ReturnsFalse() throws Exception {
        String tableName = "getEntityNoEntity";
        Registry registry = sampleData.sampleRegistry(tableName);
        registryManager.createRegistry(registryMetadataTableName, registry);

        String entityId = "nonExistingEntityId";
        entityManager.getEntity(tableName, entityId);
    }

    @Test(expected = RegistryNotFoundException.class)
    public void getEntity_RegistryNotExisting_ThrowsException() throws Exception {
        String tableName = "getEntityNoRegistry";

        String entityId = "nonExistingEntityId";
        entityManager.getEntity(tableName, entityId);
    }

    @Test
    public void updateEntity_EntityExisting_EntityUpdated() throws Exception {
        String tableName = "updateEntity";
        Registry registry = sampleData.sampleRegistry(tableName);
        registryManager.createRegistry(registryMetadataTableName, registry);

        Entity entity = sampleData.sampleEntity();

        Entity addEntity = entityManager.addEntity(tableName, entity);

        String updatedLabel = "An updated label";
        addEntity.getBody().put("label", updatedLabel);

        Entity updateEntity =
                entityManager.updateEntity(tableName, addEntity);
        assertEquals(addEntity, updateEntity);

        Entity readEntity = entityManager.getEntity(tableName, updateEntity.getId());
        String readLabel = readEntity.getBody().get("label").asText();
        assertThat(readLabel, equalTo(updatedLabel));

    }

    @Test(expected = EntityNotFoundException.class)
    public void updateEntity_EntityNotExisting_ThrowsException() throws Exception {
        String tableName = "updateEntityNoEntity";
        Registry registry = sampleData.sampleRegistry(tableName);
        registryManager.createRegistry(registryMetadataTableName, registry);

        String entityId = "nonExistingEntityId";

        Entity entity = sampleData.sampleEntity();
        String updatedLabel = "An updated label";
        entity.getBody().put("label", updatedLabel);

        entityManager.updateEntity(tableName, entity);
    }

    @Test(expected = RegistryNotFoundException.class)
    public void updateEntity_RegistryNotExisting_ThrowsException() throws Exception {
        String tableName = "updateEntityNoRegistry";
        Entity entity = sampleData.sampleEntity();
        String updatedLabel = "An updated label";
        entity.getBody().put("label", updatedLabel);

        entityManager.updateEntity(tableName, entity);
    }


    private void updateRegistrySchema(Registry registry) throws IOException, ShaclModelValidationException, TargetClassPropertyObjectIsNotAResourceException {
        registryManager.updateRegistrySchema(registryMetadataTableName, registry.getId(),
            sampleData.getValidValidationSchemaString());
    }

}
