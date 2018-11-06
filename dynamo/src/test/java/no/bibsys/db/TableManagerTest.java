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

        EntityManager entityManager = new EntityManager(newTableDriver(), TableManager.getValidationSchemaTable());
        assertThat(entityManager.getEntry(tableName).isPresent(),is(equalTo(true)));

        tableManager.deleteTable(tableName);
        assertThat(entityManager.getEntry(tableName).isPresent(),is(equalTo(false)));

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
        EntityManager tableWriter = new EntityManager(tableDriver, tableName);
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
        EntityManager tableWriter = new EntityManager(tableDriver, tableName);
        tableWriter.addEntry(new IdOnlyEntry("Id1"));
        tableManager.emptyTable(tableName);

        EntityManager entityManager = new EntityManager(tableDriver,TableManager.getValidationSchemaTable()); 
        Optional<String> schema = entityManager.getEntry(tableName);
        assertThat(schema.isPresent(),is(equalTo(true)));
    }


    @Test(expected = TableNotFoundException.class)
    public void tableManagerShouldThrowAnExceptionWhenEmptyingANonExistentTable()
        throws InterruptedException {
        TableDriver tableDriver = newTableDriver();
        TableManager tableManager = new TableManager(tableDriver);
        tableManager.emptyTable(tableName);


    }

    @Test
    public void tableManagerShouldListAllRegistries() throws JsonProcessingException, InterruptedException {
        TableDriver tableDriver = newTableDriver();
        TableManager tableManager = new TableManager(tableDriver);
        template.setId("test");
        tableManager.createRegistry(template);
        template.setId("test1");
        tableManager.createRegistry(template);
        template.setId("test2");
        tableManager.createRegistry(template);
        template.setId("test3");
        tableManager.createRegistry(template);
        template.setId("test4");
        tableManager.createRegistry(template);
        
        List<String> registries = tableManager.listRegistries();
        assertTrue(registries.contains("test"));
        assertTrue(registries.contains("test1"));
        assertTrue(registries.contains("test2"));
        assertTrue(registries.contains("test3"));
        assertTrue(registries.contains("test4"));
        
    }

    @Test 
    public void tableManagerShouldUpdateARegistry() throws InterruptedException, IOException {
        TableDriver tableDriver = newTableDriver();
        TableManager tableManager = new TableManager(tableDriver);
        String testId = "testUpdateRegistry";
        template.setId(testId);
        String descriptionBefore = "Test of update before";
        template.getMetadata().setDescription(descriptionBefore);
        tableManager.createRegistry(template);
        
        EntityManager entityManager = new EntityManager(tableDriver, TableManager.getValidationSchemaTable());
        Optional<String> entry = entityManager.getEntry(testId);
        
        assertTrue(entry.isPresent());

        ObjectMapper mapper = new ObjectMapper();
        EntityRegistryTemplate entity = mapper.readValue(entry.get(), EntityRegistryTemplate.class);
        assertThat(entity.getMetadata().getDescription(), is(equalTo(descriptionBefore)));
        
        EntityRegistryTemplate template2 = new EntityRegistryTemplate(testId);
        String descriptionAfter = "Test of update after";
        template2.getMetadata().setDescription(descriptionAfter);
        entityManager.updateJson(mapper.writeValueAsString(template2));
        
        Optional<String> entry2 = entityManager.getEntry(testId);
        assertTrue(entry2.isPresent());
        EntityRegistryTemplate entity2 = mapper.readValue(entry2.get(), EntityRegistryTemplate.class);
        assertThat(entity2.getMetadata().getDescription(), is(equalTo(descriptionAfter)));
    }
}
