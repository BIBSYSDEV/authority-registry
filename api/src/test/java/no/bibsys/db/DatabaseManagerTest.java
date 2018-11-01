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
import no.bibsys.db.exceptions.ItemExistsException;
import no.bibsys.testtemplates.LocalDynamoTest;
import no.bibsys.testtemplates.SampleData.Entry;
import no.bibsys.web.model.EditRegistryRequest;


public class DatabaseManagerTest extends LocalDynamoTest {

    private ObjectMapper mapper = new ObjectMapper();
    private static String TABLE_NAME = "DatabaseManagerTest";
    private EditRegistryRequest createRequest = new EditRegistryRequest(TABLE_NAME);

    private EditRegistryRequest createTestEditRequest() {
        EditRegistryRequest newCreateRequest = new EditRegistryRequest(TABLE_NAME);
        newCreateRequest.setContributor(Arrays.asList(new String[] {"contributor1", "contributor2"}));
        newCreateRequest.setCreator(Arrays.asList(new String[] {"creator1", "creator2"}));
        newCreateRequest.setLabel(Arrays.asList(new String[] {"label1", "label2"}));
        newCreateRequest.setSameAs(Arrays.asList(new String[] {"sameAs1", "sameAs2"}));
        newCreateRequest.setDescription("description");
        newCreateRequest.setLicense("license");
        
        return newCreateRequest;
    }
    
    @Before
    public void init() {
        createRequest = new EditRegistryRequest(TABLE_NAME);
    }
    
    @Test
    public void databaseManagerShouldCreateATable()
            throws InterruptedException, JsonProcessingException {

        boolean existsBeforeCreation = databaseManager.registryExists(TABLE_NAME);
        
        createRequest = createTestEditRequest();
        
        databaseManager.createRegistry(createRequest);
        boolean existsAfterCreation = databaseManager.registryExists(TABLE_NAME);
        boolean existsInSchemaTableAfterCreation = databaseManager.registryExists(TableManager.getValidationSchemaTable());
        assertFalse(existsBeforeCreation);
        assertTrue(existsAfterCreation);
        assertTrue(existsInSchemaTableAfterCreation);

        Optional<String> entry = databaseManager.readEntry(TableManager.getValidationSchemaTable(), TABLE_NAME);
        assertTrue(entry.toString().contains(TABLE_NAME));
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

        boolean existsBeforeCreation = databaseManager.registryExists(TABLE_NAME);
        assertThat("The table should not exist before creation", existsBeforeCreation,
                is(equalTo(false)));
        databaseManager.createRegistry(createRequest);
        boolean existsAfterCreation = databaseManager.registryExists(TABLE_NAME);
        assertThat("The table should  exist before creation", existsAfterCreation,
                is(equalTo(true)));

        databaseManager.createRegistry(createRequest);


    }


    @Test
    public void databaseManagerShouldInsertAJsonObjectIntoATable()
            throws IOException, InterruptedException {

        Entry entry = sampleData.sampleEntry("databaseManagerInsertTestId");
        databaseManager.createRegistry(createRequest);
        databaseManager.addEntry(TABLE_NAME, entry.jsonString());
        String readJson = databaseManager.readEntry(TABLE_NAME, entry.id).orElse(null);
        ObjectNode actual = mapper.readValue(readJson, ObjectNode.class);
        ObjectNode expected = mapper.readValue(entry.jsonString(), ObjectNode.class);

        assertThat(actual, is(equalTo(expected)));

    }


    @Test(expected = ItemExistsException.class)
    public void databaseManagerShouldThrowExceptionForInsertingDuplicateIds()
            throws InterruptedException, JsonProcessingException {
        Entry entry = sampleData.sampleEntry("databaseManagerInsertTestId");
        databaseManager.createRegistry(createRequest);
        databaseManager.addEntry(TABLE_NAME, entry.jsonString());
        ObjectNode root2 = entry.root.deepCopy();
        root2.put("newText", "Some new stuff");
        Entry entry2 = new Entry(entry.id, root2);
        databaseManager.addEntry(TABLE_NAME, entry2.jsonString());
    }
}
