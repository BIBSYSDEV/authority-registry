package no.bibsys.db;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import no.bibsys.testtemplates.LocalDynamoTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

public class DatabaseManagerTest extends LocalDynamoTest {

    @Autowired
    DatabaseManager databaseManager;


    @Autowired
    TableDriver tableDriver;


    @Test
    @DirtiesContext
    public void databaseManagerShouldCreateATable()
        throws InterruptedException {
        String tableName = "DBManagerTest";
        boolean existsBeforeCreation = databaseManager.registryExists(tableName);

        databaseManager.createRegistry(tableName);
        boolean existsAfterCreation = databaseManager.registryExists(tableName);

        assertThat(existsBeforeCreation, is(equalTo(false)));
        assertThat(existsAfterCreation, is(equalTo(true)));

    }


    @Test(expected = TableAlreadyExistsException.class)
    @DirtiesContext
    public void databaseManagerShouldThrowAnExceptionWhenTryingToCreateAnExistingTable()
        throws InterruptedException {
        String tableName = "DBManagerTest";
        boolean existsBeforeCreation = databaseManager.registryExists(tableName);
        assertThat("The table should not exist before creation",
            existsBeforeCreation, is(equalTo(false)));
        databaseManager.createRegistry(tableName);
        boolean existsAfterCreation = databaseManager.registryExists(tableName);
        assertThat("The table should  exist before creation",
            existsAfterCreation, is(equalTo(true)));

        databaseManager.createRegistry(tableName);


    }


    @Test
    @DirtiesContext
    public void databaseManagerShouldInsertAJsonObjectIntoATable()
        throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        String id = "InsertTestId";

        ObjectNode root = mapper.getNodeFactory().objectNode();
        root.put("id", id);
        root.put("name", "InsertTestName");
        String writeJson = mapper.writeValueAsString(root);
        String tableName = "insertTest";
        databaseManager.createRegistry(tableName);
        databaseManager.insert(tableName, writeJson);

        String readJson = databaseManager.readEntry(tableName, id);
        ObjectNode actual = mapper.readValue(readJson, ObjectNode.class);
        assertThat(actual, is(equalTo(root)));

    }


    @Test
    @DirtiesContext
    public void databaseManagerShouldCheckIfARegistryExists()
        throws InterruptedException, TableAlreadyExistsException {
        databaseManagerShouldCreateATable();
    }


}
