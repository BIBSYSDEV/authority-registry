package no.bibsys.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.bibsys.db.structures.Entity;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TableDriverTest extends LocalDynamoTest {

    @Test(expected = IllegalStateException.class)
    public void constructor_nullValue_exception() {
        TableDriver tableDriver = new TableDriver(null);

    }

    @Test
    public void createTable_TableNotExisting_AddsTable() {
        TableDriver tableDriver = newTableDriver();
        tableDriver.createEntityRegistryTable(tableName);
        List<String> tables = tableDriver.listTables();
        int numberOfTables = tables.size();

        assertThat(numberOfTables, is(equalTo(1)));
    }

    @Test
    public void createTable_TableExists_ReturnsFalse() {
        TableDriver tableDriver = newTableDriver();
        int tables = tableDriver.listTables().size();
        assertThat(tables, is(equalTo(0)));

        tableDriver.createEntityRegistryTable(tableName);
        boolean createDuplicateTable = tableDriver.createEntityRegistryTable(tableName);
        assertThat(createDuplicateTable, is(equalTo(false)));

        tables = tableDriver.listTables().size();
        assertThat(tables, is(equalTo(1)));
    }

    @Test
    public void deleteTable_EmptyTable_ReturnsTrue() {
        TableDriver tableDriver = newTableDriver();
        tableDriver.createEntityRegistryTable(tableName);

        boolean deleteTable = tableDriver.deleteTable(tableName);
        assertThat(deleteTable, equalTo(true));

        int tables = tableDriver.listTables().size();
        assertThat(tables, is(equalTo(0)));
    }

    @Test
    public void deleteTable_TableNotExisting_ReturnsFalse() {
        TableDriver tableDriver = newTableDriver();
        tableDriver.createEntityRegistryTable(tableName);

        boolean deleteTable = tableDriver.deleteTable(tableName + "blabla");
        assertThat(deleteTable, equalTo(false));

        int tables = tableDriver.listTables().size();

        assertThat(tables, is(equalTo(1)));
    }

    @Test
    public void deleteTable_TableNotEmpty_ReturnsTrue() throws JsonProcessingException {
        TableDriver tableDriver = newTableDriver();
        tableDriver.createEntityRegistryTable(tableName);

        EntityManager entityManager = new EntityManager(localClient);
        entityManager.addEntity(tableName, new Entity());

        boolean deleteTable = tableDriver.deleteTable(tableName);
        assertThat(deleteTable, equalTo(true));
    }

    @Test
    public void listTable_FiveExistingTables_ListsAllFiveTables() {
        TableDriver tableDriver = newTableDriver();
        tableDriver.createEntityRegistryTable("test");
        tableDriver.createEntityRegistryTable("test1");
        tableDriver.createEntityRegistryTable("test2");
        tableDriver.createEntityRegistryTable("test3");
        tableDriver.createEntityRegistryTable("test4");

        List<String> tables = tableDriver.listTables();
        assertTrue(tables.contains("test"));
        assertTrue(tables.contains("test1"));
        assertTrue(tables.contains("test2"));
        assertTrue(tables.contains("test3"));
        assertTrue(tables.contains("test4"));

    }
}
