package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.amazonaws.services.dynamodbv2.model.TableNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import no.bibsys.db.exceptions.ItemExistsException;
import no.bibsys.db.structures.ValidationSchemaEntry;


public class TableManager {

    private final transient TableDriver tableDriver;


    public TableManager(final TableDriver tableDriver) {
        this.tableDriver = tableDriver;
    }

    public void deleteTable(final String tableName) throws InterruptedException {
        try {
            tableDriver.deleteTable(tableName);
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
        } else {
            throw new TableNotFoundException(tableName);
        }
    }

    public void createRegistry(String tableName, String validationSchema)
        throws InterruptedException, JsonProcessingException {
        tableDriver.createTable(tableName);

    }

    public boolean tableExists(String tableName){
        return this.tableDriver.tableExists(tableName);
    }


}
