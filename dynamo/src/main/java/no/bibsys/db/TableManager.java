package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.amazonaws.services.dynamodbv2.model.TableNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.bibsys.db.structures.EntityRegistryTemplate;


public class TableManager {

    private final transient TableDriver tableDriver;
    private final transient ObjectMapper objectMapper;

    public TableManager(final TableDriver tableDriver) {
        this.tableDriver = tableDriver;
        objectMapper = ObjectMapperHelper.getObjectMapper();

    }

    public void deleteTable(final String tableName) throws InterruptedException {
        try {
            tableDriver.deleteTable(tableName);
            TableWriter writer = new TableWriter(tableDriver, getValidationSchemaTable());
            writer.deleteEntry(tableName);
        } catch (ResourceNotFoundException e) {
            throw new TableNotFoundException(e.getMessage());
        }
    }


    public AmazonDynamoDB getClient() {
        return tableDriver.getClient();
    }

    /**
     * Creates new table.
     *
     * @param tableName The name of the table.
     * @throws InterruptedException when operation is interrupted
     */


    /**
     * Fastest way to empty a table. Delete a table and create it with the same validation schema.
     * This method does not delete the validation schema of the table
     */
    public void emptyTable(final String tableName) throws InterruptedException {
        if (tableDriver.tableExists(tableName)) {
            tableDriver.deleteNoCheckTable(tableName);
            tableDriver.createTable(tableName);
        } else {
            throw new TableNotFoundException(tableName);
        }
    }

    public void createRegistry(EntityRegistryTemplate template)
            throws InterruptedException, JsonProcessingException {

        if(!tableExists(getValidationSchemaTable())) {
            tableDriver.createTable(getValidationSchemaTable());
        }

        String tableName = template.getId();

        if(!tableExists(tableName)) {
            TableWriter writer = new TableWriter(tableDriver, getValidationSchemaTable());

            writer.addJson(objectMapper.writeValueAsString(template));

            tableDriver.createTable(tableName);     
        }else {
            throw new TableAlreadyExistsException(String.format("Table %s already exists", tableName));
        }

    }

    public boolean tableExists(String tableName){
        return this.tableDriver.tableExists(tableName);
    }

    public static String getValidationSchemaTable() {
        String validationSchemaTableName = "VALIDATION_SCHEMA_TABLE";
        String stage = System.getenv("STAGE_NAME");
        if("test".equals(stage)) {
            validationSchemaTableName = String.join("_", "TEST", validationSchemaTableName);
        }

        return validationSchemaTableName;
    }
    
}
