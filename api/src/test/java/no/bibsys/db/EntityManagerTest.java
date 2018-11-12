package no.bibsys.db;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;

import no.bibsys.db.structures.EntityRegistryTemplate;
import no.bibsys.testtemplates.LocalDynamoTest;
import no.bibsys.testtemplates.SampleData.Entry;

public class EntityManagerTest extends LocalDynamoTest{

    @Test
    public void addEntityToEmptyRegistryReturnsTrue() throws IOException {
        
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
    public void addEntityToNonExistingRegistryReturnsFalse() throws IOException {
        String tableName = "addEntityNoRegistry";
        Entry entry = sampleData.sampleEntry();
        Optional<String> addEntity = entityManager.addEntity(tableName, entry.jsonString());
        assertThat(addEntity.isPresent(), equalTo(false));
    }
    
    @Test
    public void deleteEntityToExistingEntityReturnsTrue() throws IOException {
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
    public void deleteEntityToNonExistingEntityReturnsFalse() throws IOException {
        String tableName = "deleteEntityNoEntity";
        EntityRegistryTemplate testTemplate = new EntityRegistryTemplate(tableName);
        registryManager.createRegistryFromTemplate(testTemplate);
        
        String entityId = "nonExistingEntityId";
        boolean deleteEntity = entityManager.deleteEntity(tableName, entityId);
        assertThat(deleteEntity, equalTo(false));
    }
    
    @Test
    public void deleteEntityToNonExistingRegistryReturnsFalse() throws IOException {
        String tableName = "deleteEntityNoRegistry";
        
        String entityId = "nonExistingEntityId";
        boolean deleteEntity = entityManager.deleteEntity(tableName, entityId);
        assertThat(deleteEntity, equalTo(false));
    }
    
    @Test
    public void entityExistsToExistingEntityReturnsTrue() throws IOException {
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
    public void entityExistsToNonExistingEntityReturnsFalse() throws IOException {
        String tableName = "entityExistsNoEntity";
        EntityRegistryTemplate testTemplate = new EntityRegistryTemplate(tableName);
        registryManager.createRegistryFromTemplate(testTemplate);
        
        String entityId = "nonExistingEntity";
        boolean entityExists = entityManager.entityExists(tableName, entityId);
        assertThat(entityExists, equalTo(false));
    }
    
    @Test
    public void entityExistsToNonExistingRegistryReturnsFalse() throws IOException {
        String tableName = "entityExistsNoRegistry";
        
        String entityId = "nonExistingEntity";
        boolean entityExists = entityManager.entityExists(tableName, entityId);
        assertThat(entityExists, equalTo(false));
    }
    
    @Test
    public void getEntityToExistingEntitReturnsTrue() throws IOException {
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
    public void getEntityToNonExistingEntityReturnsFalse() throws IOException {
        String tableName = "getEntityNoEntity";
        EntityRegistryTemplate testTemplate = new EntityRegistryTemplate(tableName);
        registryManager.createRegistryFromTemplate(testTemplate);
        
        String entityId = "nonExistingEntityId";
        Optional<String> getEntity = entityManager.getEntity(tableName, entityId);
        assertThat(getEntity.isPresent(), equalTo(false));
    }
    
    @Test
    public void getEntityToNonExistingRegistryReturnsFalse() throws IOException {
        String tableName = "getEntityNoRegistry";
        
        String entityId = "nonExistingEntityId";
        Optional<String> getEntity = entityManager.getEntity(tableName, entityId);
        assertThat(getEntity.isPresent(), equalTo(false));
    }

    @Test
    public void updateEntityToExistingEntityReturnsTrue() throws IOException {
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
        Optional<String> updateEntity = entityManager.updateEntity(tableName, updateEntry.jsonString());
        assertThat(updateEntity.isPresent(), equalTo(true));
        
        Optional<String> readEntity = entityManager.getEntity(tableName, entityId);
        String readLabel = ObjectMapperHelper.getObjectMapper().readTree(readEntity.get()).get("label").asText();
        assertThat(readLabel, equalTo(updatedLabel));
        
    }
    
    @Test
    public void updateEntityToNonExistingEntityReturnsFalse() throws IOException {
        String tableName = "updateEntityNoEntity";
        EntityRegistryTemplate testTemplate = new EntityRegistryTemplate(tableName);
        registryManager.createRegistryFromTemplate(testTemplate);

        String entityId = "nonExistingEntityId";
        
        Entry updateEntry = sampleData.sampleEntry();
        String updatedLabel = "An updated label";
        updateEntry.root.put("id", entityId);
        updateEntry.root.put("label", updatedLabel);
        Optional<String> updateEntity = entityManager.updateEntity(tableName, updateEntry.jsonString());
        assertThat(updateEntity.isPresent(), equalTo(false));
    }
    
    @Test
    public void updateEntityToNonExistingRegistryReturnsFalse() {
        String tableName = "updateEntityNoRegistry";
        
        String entityId = "nonExistingEntityId";
        
        Entry updateEntry = sampleData.sampleEntry();
        String updatedLabel = "An updated label";
        updateEntry.root.put("id", entityId);
        updateEntry.root.put("label", updatedLabel);
        Optional<String> updateEntity = entityManager.updateEntity(tableName, updateEntry.jsonString());
        assertThat(updateEntity.isPresent(), equalTo(false));
    }
//    entityManager.updateEntity(registryName, entity)

}
