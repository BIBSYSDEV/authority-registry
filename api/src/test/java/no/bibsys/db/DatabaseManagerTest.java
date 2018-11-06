package no.bibsys.db;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import no.bibsys.db.structures.EntityRegistryTemplate;
import no.bibsys.testtemplates.LocalDynamoTest;
import no.bibsys.testtemplates.SampleData.Entry;


public class DatabaseManagerTest extends LocalDynamoTest {

    private ObjectMapper mapper = ObjectMapperHelper.getObjectMapper();

    private EntityRegistryTemplate createTestEditRequest(String tableName) {
        EntityRegistryTemplate newCreateRequest = new EntityRegistryTemplate();
        newCreateRequest.setId(tableName);
        newCreateRequest.getMetadata().setContributor(Arrays.asList("contributor1", "contributor2"));
        newCreateRequest.getMetadata().setCreator(Arrays.asList("creator1", "creator2"));
        newCreateRequest.getMetadata().setLabel(Arrays.asList("label1", "label2"));
        newCreateRequest.getMetadata().setSameAs(Arrays.asList("sameAs1", "sameAs2"));
        newCreateRequest.getMetadata().setDescription("description");
        newCreateRequest.getMetadata().setLicense("license");
        
        return newCreateRequest;
    }
    
    @Test
    public void databaseManagerShouldCreateATable() 
            throws InterruptedException, JsonProcessingException {

        String tableName = "createATable";
        boolean existsBeforeCreation = registryManager.registryExists(tableName);
        
        EntityRegistryTemplate createRequest = createTestEditRequest(tableName);
        
        registryManager.createRegistry(createRequest);
        boolean existsAfterCreation = registryManager.registryExists(tableName);
        assertFalse(existsBeforeCreation);
        assertTrue(existsAfterCreation);

        Optional<String> entry = databaseManager.getEntry(TableManager.getValidationSchemaTable(), tableName);
        assertThat(entry.isPresent(), is(equalTo(true)));
        assertTrue(entry.toString().contains(tableName));
        assertTrue(entry.toString().contains("creator1"));
        assertTrue(entry.toString().contains("contributor2"));
        assertTrue(entry.toString().contains("label1"));
        assertTrue(entry.toString().contains("sameAs2"));
        assertTrue(entry.toString().contains("description"));
        assertTrue(entry.toString().contains("license"));
    }


    @Test(expected = TableAlreadyExistsException.class)
    public void databaseManagerShouldThrowAnExceptionWhenTryingToCreateAnExistingTable()
            throws InterruptedException, JsonProcessingException {

        String tableName = "tabelAlreadyExists";
        boolean existsBeforeCreation = registryManager.registryExists(tableName );
        assertThat("The table should not exist before creation", existsBeforeCreation,
                is(equalTo(false)));
        EntityRegistryTemplate createRequest = createTestEditRequest(tableName);
        registryManager.createRegistry(createRequest );
        boolean existsAfterCreation = registryManager.registryExists(tableName);
        assertThat("The table should  exist before creation", existsAfterCreation,
                is(equalTo(true)));

        registryManager.createRegistry(createRequest);


    }


    @Test
    public void databaseManagerShouldInsertAJsonObjectIntoATable()
            throws IOException, InterruptedException {

        String tableName = "insertJsonObject";
        Entry entry = sampleData.sampleEntry("databaseManagerInsertTestId");
        EntityRegistryTemplate createRequest = createTestEditRequest(tableName);
        registryManager.createRegistry(createRequest );
        String entityId = databaseManager.addEntry(tableName , entry.jsonString());
        String readJson = databaseManager.getEntry(tableName, entityId).orElse(null);
        String actual = mapper.readValue(readJson, ObjectNode.class).get("id").asText();

        assertThat(actual, is(equalTo(entityId)));

    }


    @Test(expected = TableAlreadyExistsException.class)
    public void databaseManagerShouldThrowExceptionForInsertingDuplicateIds() 
            throws InterruptedException, JsonProcessingException {
        String tableName = "tableAlreadyExists";
        Entry entry = sampleData.sampleEntry("databaseManagerInsertTestId");
        EntityRegistryTemplate createRequest = createTestEditRequest(tableName);
        registryManager.createRegistry(createRequest );
        registryManager.addRegistry(tableName, entry.jsonString());
        ObjectNode root2 = entry.root.deepCopy();
        root2.put("newText", "Some new stuff");
        Entry entry2 = new Entry(entry.id, root2);
        registryManager.addRegistry(tableName, entry2.jsonString());
    }
    
    @Test
    public void databaseManagerAddSchemaToRegistry() throws TableAlreadyExistsException, InterruptedException, IOException {
        String tableName = "addSchemaToRegistry";
        EntityRegistryTemplate createRequest = createTestEditRequest(tableName);
        registryManager.createRegistry(createRequest);
        String schemaAsJson = "JSON validation schema";
        registryManager.setSchemaJson(tableName, schemaAsJson);
        
        assertThat(registryManager.getSchemaAsJson(tableName), is(equalTo(schemaAsJson)));
    }
}
