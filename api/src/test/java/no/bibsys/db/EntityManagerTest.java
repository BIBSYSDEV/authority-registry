package no.bibsys.db;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import no.bibsys.db.structures.EntityRegistryTemplate;
import no.bibsys.testtemplates.LocalDynamoTest;
import no.bibsys.testtemplates.SampleData.Entry;

public class EntityManagerTest extends LocalDynamoTest{

    @Test
    public void addEntityToEmptyRegistrySucceeds() throws IOException {
        
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
    public void addEntityToNonExistingRegistryFails() throws IOException {
        String tableName = "addEntityNoRegistry";
        Entry entry = sampleData.sampleEntry();
        Optional<String> addEntity = entityManager.addEntity(tableName, entry.jsonString());
        assertThat(addEntity.isPresent(), equalTo(false));
    }
    
    @Test
    public void deleteEntityToExistingEntitySucceeds() throws IOException {
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
    public void deleteEntityToNonExistingEntityFails() throws IOException {
        String tableName = "deleteEntityNoEntity";
        EntityRegistryTemplate testTemplate = new EntityRegistryTemplate(tableName);
        registryManager.createRegistryFromTemplate(testTemplate);
        
        String entityId = "nonExistingEntityId";
        boolean deleteEntity = entityManager.deleteEntity(tableName, entityId);
        assertThat(deleteEntity, equalTo(false));
    }
    
    @Test
    public void deleteEntityToNonExistingRegistryFails() throws IOException {
        String tableName = "deleteEntityNoRegistry";
        
        String entityId = "nonExistingEntityId";
        boolean deleteEntity = entityManager.deleteEntity(tableName, entityId);
        assertThat(deleteEntity, equalTo(false));
    }
    
    @Test
    public void entityExistsToExistingEntitySucceeds() throws IOException {
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
    public void entityExistsToNonExistingEntityFails() throws IOException {
        String tableName = "entityExistsNoEntity";
        EntityRegistryTemplate testTemplate = new EntityRegistryTemplate(tableName);
        registryManager.createRegistryFromTemplate(testTemplate);
        
        String entityId = "nonExistingEntity";
        boolean entityExists = entityManager.entityExists(tableName, entityId);
        assertThat(entityExists, equalTo(false));
    }
    
    @Test
    public void entityExistsToNonExistingRegistryFails() throws IOException {
        String tableName = "entityExistsNoRegistry";
        
        String entityId = "nonExistingEntity";
        boolean entityExists = entityManager.entityExists(tableName, entityId);
        assertThat(entityExists, equalTo(false));
    }
    
    @Test
    public void getEntityToExistingEntitySucceeds() throws IOException {
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
    public void getEntityToNonExistingEntityFails() throws IOException {
        String tableName = "getEntityNoEntity";
        EntityRegistryTemplate testTemplate = new EntityRegistryTemplate(tableName);
        registryManager.createRegistryFromTemplate(testTemplate);
        
        String entityId = "nonExistingEntityId";
        Optional<String> getEntity = entityManager.getEntity(tableName, entityId);
        assertThat(getEntity.isPresent(), equalTo(false));
    }
    
    @Test
    public void getEntityToNonExistingRegistryFails() throws IOException {
        String tableName = "getEntityNoRegistry";
        
        String entityId = "nonExistingEntityId";
        Optional<String> getEntity = entityManager.getEntity(tableName, entityId);
        assertThat(getEntity.isPresent(), equalTo(false));
    }

    @Test
    public void updateEntityToExistingEntitySucceeds() throws IOException {
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
    public void updateEntityToNonExistingEntityFails() throws IOException {
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
    public void updateEntityToNonExistingRegistryFails() {
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
