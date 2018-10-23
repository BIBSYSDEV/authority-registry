package no.bibsys.db;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import org.junit.Test;
import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import no.bibsys.db.exceptions.ItemExistsException;
import no.bibsys.testtemplates.LocalDynamoTest;
import no.bibsys.testtemplates.SampleData.Entry;
import no.bibsys.web.model.CreateRegistryRequest;


public class DatabaseManagerTest extends LocalDynamoTest {

    private ObjectMapper mapper = new ObjectMapper();
    private static String TABLE_NAME = "DatabaseManagerTest";
    private static CreateRegistryRequest CREATE_REQUEST = new CreateRegistryRequest(TABLE_NAME);

    @Test
    public void databaseManagerShouldCreateATable()
            throws InterruptedException, JsonProcessingException {

        boolean existsBeforeCreation = databaseManager.registryExists(TABLE_NAME);
        
        databaseManager.createRegistry(CREATE_REQUEST);
        boolean existsAfterCreation = databaseManager.registryExists(TABLE_NAME);

        assertThat(existsBeforeCreation, is(equalTo(false)));
        assertThat(existsAfterCreation, is(equalTo(true)));

    }


    @Test(expected = TableAlreadyExistsException.class)
    public void databaseManagerShouldThrowAnExceptionWhenTryingToCreateAnExistingTable()
            throws InterruptedException, JsonProcessingException {

        boolean existsBeforeCreation = databaseManager.registryExists(TABLE_NAME);
        assertThat("The table should not exist before creation", existsBeforeCreation,
                is(equalTo(false)));
        databaseManager.createRegistry(CREATE_REQUEST);
        boolean existsAfterCreation = databaseManager.registryExists(TABLE_NAME);
        assertThat("The table should  exist before creation", existsAfterCreation,
                is(equalTo(true)));

        databaseManager.createRegistry(CREATE_REQUEST);


    }


    @Test
    public void databaseManagerShouldInsertAJsonObjectIntoATable()
            throws IOException, InterruptedException {

        Entry entry = sampleData.sampleEntry("databaseManagerInsertTestId");
        databaseManager.createRegistry(CREATE_REQUEST);
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
        databaseManager.createRegistry(CREATE_REQUEST);
        databaseManager.addEntry(TABLE_NAME, entry.jsonString());
        ObjectNode root2 = entry.root.deepCopy();
        root2.put("newText", "Some new stuff");
        Entry entry2 = new Entry(entry.id, root2);
        databaseManager.addEntry(TABLE_NAME, entry2.jsonString());
    }
}
