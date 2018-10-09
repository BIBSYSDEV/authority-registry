package no.bibsys.db;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import no.bibsys.db.exceptions.ItemExistsException;
import no.bibsys.testtemplates.LocalDynamoTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

public class DatabaseManagerTest extends LocalDynamoTest {

    @Autowired
    DatabaseManager databaseManager;


    @Autowired
    TableDriver tableDriver;

    private ObjectMapper mapper = new ObjectMapper();
    private String tableName = "DatabaseManagerTest";
    private String validationSchema="validationSchema";

    @Test
    @DirtiesContext
    public void databaseManagerShouldCreateATable()
        throws InterruptedException, JsonProcessingException {

        boolean existsBeforeCreation = databaseManager.registryExists(tableName);

        databaseManager.createRegistry(tableName,validationSchema);
        boolean existsAfterCreation = databaseManager.registryExists(tableName);

        assertThat(existsBeforeCreation, is(equalTo(false)));
        assertThat(existsAfterCreation, is(equalTo(true)));

    }


    @Test(expected = TableAlreadyExistsException.class)
    @DirtiesContext
    public void databaseManagerShouldThrowAnExceptionWhenTryingToCreateAnExistingTable()
        throws InterruptedException, JsonProcessingException {

        boolean existsBeforeCreation = databaseManager.registryExists(tableName);
        assertThat("The table should not exist before creation",
            existsBeforeCreation, is(equalTo(false)));
        databaseManager.createRegistry(tableName,validationSchema);
        boolean existsAfterCreation = databaseManager.registryExists(tableName);
        assertThat("The table should  exist before creation",
            existsAfterCreation, is(equalTo(true)));

        databaseManager.createRegistry(tableName,validationSchema);


    }


    @Test
    @DirtiesContext
    public void databaseManagerShouldInsertAJsonObjectIntoATable()
        throws IOException, InterruptedException {

        Entry entry = sampleEntry("databaseManagerInsertTestId");
        databaseManager.createRegistry(tableName,validationSchema);
        databaseManager.insertEntry(tableName, entry.jsonString());
        String readJson = databaseManager.readEntry(tableName, entry.id).orElse(null);
        ObjectNode actual = mapper.readValue(readJson, ObjectNode.class);
        ObjectNode expected = mapper.readValue(entry.jsonString(), ObjectNode.class);

        assertThat(actual, is(equalTo(expected)));

    }


    @Test(expected = ItemExistsException.class)
    @DirtiesContext
    public void databaseManagerShouldThrowExceptionForInsertingDuplicateIds()
        throws InterruptedException, JsonProcessingException {
        Entry entry = sampleEntry("databaseManagerInsertTestId");
        databaseManager.createRegistry(tableName,validationSchema);
        databaseManager.insertEntry(tableName, entry.jsonString());
        ObjectNode root2 = entry.root.deepCopy();
        root2.put("newText", "Some new stuff");
        Entry entry2 = new Entry(entry.id, root2);
        databaseManager.insertEntry(tableName, entry2.jsonString());


    }





}
