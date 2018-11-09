package no.bibsys.db;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

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
    public void registryManagerCreatesANewRegistry()
        throws InterruptedException, IOException {

        String tableName = "createARegistry";
        boolean existsBeforeCreation = registryManager.registryExists(tableName);
        
        EntityRegistryTemplate createRequest = createTestEditRequest(tableName);
        
        registryManager.createRegistry(createRequest);
        boolean existsAfterCreation = registryManager.registryExists(tableName);
        assertFalse(existsBeforeCreation);
        assertTrue(existsAfterCreation);

        assertThat(registryManager.registryExists(tableName), is(equalTo(true)));

        EntityRegistryTemplate metadata = registryManager.getRegistryMetadata(tableName);
        
        assertThat(metadata.getId(), is(equalTo(tableName)));

        assertThat(metadata.getMetadata().getCreator().get(0), is(equalTo("creator1")));
        assertThat(metadata.getMetadata().getCreator().get(1), is(equalTo("creator2")));
        assertThat(metadata.getMetadata().getContributor().get(0), is(equalTo("contributor1")));
        assertThat(metadata.getMetadata().getContributor().get(1), is(equalTo("contributor2")));
        assertThat(metadata.getMetadata().getLabel().get(0), is(equalTo("label1")));
        assertThat(metadata.getMetadata().getLabel().get(1), is(equalTo("label2")));
        assertThat(metadata.getMetadata().getSameAs().get(0), is(equalTo("sameAs1")));
        assertThat(metadata.getMetadata().getSameAs().get(1), is(equalTo("sameAs2")));
        assertThat(metadata.getMetadata().getDescription(), is(equalTo("description")));
        assertThat(metadata.getMetadata().getLicense(), is(equalTo("license")));
        assertThat(metadata.getMetadata().getCreateDate().getTime(), is(greaterThan(0L)));
        
    }


    @Test
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
        Optional<String> entityId = entityManager.addEntity(tableName , entry.jsonString());
        String readJson = entityManager.getEntity(tableName, entityId.get()).orElse(null);
        String actual = mapper.readValue(readJson, ObjectNode.class).get("id").asText();

        assertThat(actual, is(equalTo(entityId.get())));

    }


    @Test
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
        
        assertThat(registryManager.getSchemaAsJson(tableName).get(), is(equalTo(schemaAsJson)));
    }
}
