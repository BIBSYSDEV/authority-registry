package no.bibsys.db;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.amazonaws.services.dynamodbv2.model.TableNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Optional;
import no.bibsys.db.exceptions.TableNotEmptyException;
import no.bibsys.db.structures.IdOnlyEntry;
import org.junit.Test;


public class TableManagerTest extends LocalDynamoTest {

    @Test
    public void createTable() throws InterruptedException, JsonProcessingException {
        TableDriver tableDriver=newTableDriver();
        TableManager tableManager = new TableManager(tableDriver);
        tableManager.createRegistry(template);
        ListTablesResult tables = tableManager.getClient().listTables();
        int numberOfTables = tables.getTableNames().size();

        assertThat(numberOfTables, is(equalTo(2)));
    }

    @Test(expected = TableAlreadyExistsException.class)
    public void tableManagerShouldThrowExceptionWhenCreatingAnExistingTable()
        throws InterruptedException, JsonProcessingException {
        TableManager tableManager = new TableManager(newTableDriver());
        int tables = tableManager.getClient().listTables().getTableNames().size();
        assertThat(tables, is(equalTo(0)));
        tableManager.createRegistry(template);
        tableManager.createRegistry(template);
    }


    @Test
    public void tableManagerShouldDeleteAnEmptyTable()
        throws InterruptedException, JsonProcessingException {
        TableManager tableManager = new TableManager(newTableDriver());
        tableManager.createRegistry(template);

        TableReader reader = new TableReader(newTableDriver(), TableManager.getValidationSchemaTable());
        assertThat(reader.getEntry(tableName).isPresent(),is(equalTo(true)));

        tableManager.deleteTable(tableName);
        assertThat(reader.getEntry(tableName).isPresent(),is(equalTo(false)));

        int tables = tableManager.getClient().listTables().getTableNames().size();


        assertThat(tables, is(equalTo(1)));
    }


    @Test(expected = TableNotFoundException.class)
    public void tableManagerShouldThrowAnExceptionWhenDeletingAnNonExistingTable()
        throws InterruptedException, JsonProcessingException {
        TableManager tableManager = new TableManager(newTableDriver());
        tableManager.createRegistry(template);

        tableManager.deleteTable(tableName+"blabla");

        int tables = tableManager.getClient().listTables().getTableNames().size();

        assertThat(tables, is(equalTo(2)));
    }


    @Test(expected = TableNotEmptyException.class)
    public void tableManagerShouldNotDeleteNonEmptyTable()
        throws InterruptedException, JsonProcessingException {
        TableDriver tableDriver = newTableDriver();
        TableManager tableManager = new TableManager(tableDriver);
        tableManager.createRegistry(template);
        ;
        TableWriter tableWriter = new TableWriter(tableDriver, tableName);
        tableWriter.addEntry(newSimpleEntry());
        tableManager.deleteTable(tableName);

    }

    @Test(expected = TableNotFoundException.class)
    public void tableManagerShouldThrowExceptionOnDeletingNonExistingTable()
        throws InterruptedException {
        TableDriver tableDriver = newTableDriver();
        TableManager tableManager = new TableManager(tableDriver);
        tableManager.deleteTable(tableName);

    }

    @Test
    public void tableManagerShouldEmptyNonEmptyTables()
        throws InterruptedException, JsonProcessingException {
        TableDriver tableDriver = newTableDriver();
        TableManager tableManager = new TableManager(tableDriver);
        tableManager.createRegistry(template);
        TableWriter tableWriter = new TableWriter(tableDriver, tableName);
        tableWriter.addEntry(new IdOnlyEntry("Id1"));
        tableManager.emptyTable(tableName);

        TableReader reader=new TableReader(tableDriver,TableManager.getValidationSchemaTable()); 
        Optional<String> schema = reader.getEntry(tableName);
        assertThat(schema.isPresent(),is(equalTo(true)));
    }


    @Test(expected = TableNotFoundException.class)
    public void tableManagerShouldThrowAnExceptionWhenEmptyingANonExistentTable()
        throws InterruptedException {
        TableDriver tableDriver = newTableDriver();
        TableManager tableManager = new TableManager(tableDriver);
        tableManager.emptyTable(tableName);


    }




}
