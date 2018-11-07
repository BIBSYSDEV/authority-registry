package no.bibsys.db;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.amazonaws.services.dynamodbv2.model.TableNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.bibsys.db.exceptions.TableNotEmptyException;
import no.bibsys.db.structures.EntityRegistryTemplate;
import no.bibsys.db.structures.IdOnlyEntry;


public class TableManagerTest extends LocalDynamoTest {

    @Test
    public void createTable() throws InterruptedException, JsonProcessingException {
        TableDriver tableDriver=newTableDriver();
        TableManager tableManager = new TableManager(tableDriver);
        tableManager.createTable(template.getId());
        List<String> tables = tableManager.listTables();
        int numberOfTables = tables.size();

        assertThat(numberOfTables, is(equalTo(1)));
    }

    @Test(expected = TableAlreadyExistsException.class)
    public void tableManagerShouldThrowExceptionWhenCreatingAnExistingTable()
        throws InterruptedException, JsonProcessingException {
        TableManager tableManager = new TableManager(newTableDriver());
        int tables = tableManager.listTables().size();
        assertThat(tables, is(equalTo(0)));
        tableManager.createTable(template.getId());
        tableManager.createTable(template.getId());
    }


    @Test
    public void tableManagerShouldDeleteAnEmptyTable()
        throws InterruptedException, JsonProcessingException {
        TableManager tableManager = new TableManager(newTableDriver());
        tableManager.createTable(template.getId());

        tableManager.deleteTable(tableName);
        int tables = tableManager.listTables().size();

        assertThat(tables, is(equalTo(0)));
    }


    @Test(expected = TableNotFoundException.class)
    public void tableManagerShouldThrowAnExceptionWhenDeletingAnNonExistingTable()
        throws InterruptedException, JsonProcessingException {
        TableManager tableManager = new TableManager(newTableDriver());
        tableManager.createTable(template.getId());

        tableManager.deleteTable(tableName+"blabla");

        int tables = tableManager.listTables().size();

        assertThat(tables, is(equalTo(1)));
    }


    @Test(expected = TableNotEmptyException.class)
    public void tableManagerShouldNotDeleteNonEmptyTable()
        throws InterruptedException, JsonProcessingException {
        TableDriver tableDriver = newTableDriver();
        TableManager tableManager = new TableManager(tableDriver);
        tableManager.createTable(template.getId());

        ItemDriver itemDriver = newItemDriver();
        ItemManager itemManager = new ItemManager(itemDriver);
        itemManager.addJson(template.getId(), newSimpleEntry().asJson());
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
        tableManager.createTable(template.getId());
        ItemManager tableWriter = new ItemManager(newItemDriver());
        tableWriter.addJson(template.getId(), new IdOnlyEntry("Id1").asJson());
        tableManager.emptyTable(tableName);

        boolean actual = tableManager.tableExists(tableName);
        boolean expected = true;
        assertThat(actual, is(equalTo(expected)));
    }


    @Test(expected = TableNotFoundException.class)
    public void tableManagerShouldThrowAnExceptionWhenEmptyingANonExistentTable()
        throws InterruptedException {
        TableDriver tableDriver = newTableDriver();
        TableManager tableManager = new TableManager(tableDriver);
        tableManager.emptyTable(tableName);
    }

    @Test
    public void tableManagerShouldListAllTables() throws JsonProcessingException, InterruptedException {
        TableDriver tableDriver = newTableDriver();
        TableManager tableManager = new TableManager(tableDriver);
        template.setId("test");
        tableManager.createTable(template.getId());
        template.setId("test1");
        tableManager.createTable(template.getId());
        template.setId("test2");
        tableManager.createTable(template.getId());
        template.setId("test3");
        tableManager.createTable(template.getId());
        template.setId("test4");
        tableManager.createTable(template.getId());
        
        List<String> tables = tableManager.listTables();
        assertTrue(tables.contains("test"));
        assertTrue(tables.contains("test1"));
        assertTrue(tables.contains("test2"));
        assertTrue(tables.contains("test3"));
        assertTrue(tables.contains("test4"));
        
    }
}
