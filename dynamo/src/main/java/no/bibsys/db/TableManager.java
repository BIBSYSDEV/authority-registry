package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.amazonaws.services.dynamodbv2.model.TableNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import no.bibsys.db.exceptions.ItemExistsException;
import no.bibsys.db.structures.ValidationSchemaEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TableManager {

    public static final String VALIDATION_SCHEMA_TABLE = "VALIDATION_SCHEMAS";


    private final static Logger logger = LoggerFactory.getLogger(TableManager.class);

    private final transient TableDriver tableDriver;


    public TableManager(final TableDriver tableDriver) {
        this.tableDriver = tableDriver;
    }

    public void deleteTable(final String tableName) throws InterruptedException {
        try {
            tableDriver.deleteTable(tableName);
            TableWriter writer = new TableWriter(tableDriver, VALIDATION_SCHEMA_TABLE);
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
        if (!tableName.equals(VALIDATION_SCHEMA_TABLE) && tableDriver.tableExists(tableName)) {
            tableDriver.deleteNoCheckTable(tableName);
        } else {
            throw new TableNotFoundException(tableName);
        }
    }


    private void createValidationSchemaTable() throws InterruptedException {
        tableDriver.createTable(VALIDATION_SCHEMA_TABLE, new ValidationSchemaEntry());
    }


    public void createRegistry(String tableName, String validationSchema)
        throws InterruptedException, JsonProcessingException {
        if (!tableDriver.tableExists(VALIDATION_SCHEMA_TABLE)) {
            createValidationSchemaTable();
        }
        insertValidationSchema(tableName, validationSchema);

        tableDriver.createTable(tableName);

    }


    private void insertValidationSchema(String tableName, String validationSchema)
        throws JsonProcessingException {
        TableWriter tableWriter = new TableWriter(tableDriver, VALIDATION_SCHEMA_TABLE);
        try {
            tableWriter.insertEntry(new ValidationSchemaEntry(tableName, validationSchema));
        } catch (ItemExistsException e) {
            throw new TableAlreadyExistsException(e.getMessage());
        }
    }


}
