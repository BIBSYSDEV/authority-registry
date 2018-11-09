package no.bibsys.db;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;


public class TableDriverTest extends LocalDynamoTest {

    @Test
    public void createTableSucceeds() {
        TableDriver tableDriver = newTableDriver();
        tableDriver.createTable(template.getId());
        List<String> tables = tableDriver.listTables();
        int numberOfTables = tables.size();

        assertThat(numberOfTables, is(equalTo(1)));
    }

    @Test
    public void createTableWithAnExistingTableFails() {
        TableDriver tableDriver = newTableDriver();
        int tables = tableDriver.listTables().size();
        assertThat(tables, is(equalTo(0)));

        tableDriver.createTable(template.getId());
        boolean createDuplicateTable = tableDriver.createTable(template.getId());
        assertThat(createDuplicateTable, is(equalTo(false)));
        
        tables = tableDriver.listTables().size();
        assertThat(tables, is(equalTo(1)));
    }


    @Test
    public void deleteTableOnEmptyTableSucceeds() {
        TableDriver tableDriver = newTableDriver();
        tableDriver.createTable(template.getId());

        boolean deleteTable = tableDriver.deleteTable(tableName);
        assertThat(deleteTable, equalTo(true));

        int tables = tableDriver.listTables().size();
        assertThat(tables, is(equalTo(0)));
    }

    @Test
    public void deleteTableOnNonExistingTableFails()  {
        TableDriver tableDriver = newTableDriver();
        tableDriver.createTable(template.getId());

        boolean deleteTable = tableDriver.deleteTable(tableName+"blabla");
        assertThat(deleteTable, equalTo(false));
        
        int tables = tableDriver.listTables().size();

        assertThat(tables, is(equalTo(1)));
    }

    @Test
    public void deleteTableOnNonEmptyTableFails() throws JsonProcessingException {
        TableDriver tableDriver = newTableDriver();
        tableDriver.createTable(template.getId());

        ItemDriver itemDriver = newItemDriver();
        itemDriver.addItem(template.getId(), newSimpleEntry().asJson());
        boolean deleteTable = tableDriver.deleteTable(tableName);
        assertThat(deleteTable, equalTo(false));
    }


    @Test
    public void listTableListsAllTables() {
        TableDriver tableDriver = newTableDriver();
        template.setId("test");
        tableDriver.createTable(template.getId());
        template.setId("test1");
        tableDriver.createTable(template.getId());
        template.setId("test2");
        tableDriver.createTable(template.getId());
        template.setId("test3");
        tableDriver.createTable(template.getId());
        template.setId("test4");
        tableDriver.createTable(template.getId());
        
        List<String> tables = tableDriver.listTables();
        assertTrue(tables.contains("test"));
        assertTrue(tables.contains("test1"));
        assertTrue(tables.contains("test2"));
        assertTrue(tables.contains("test3"));
        assertTrue(tables.contains("test4"));
        
    }
}
