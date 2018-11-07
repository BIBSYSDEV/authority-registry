package no.bibsys.db;

import java.util.List;

import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.TableNotFoundException;


public class TableManager {

    private final transient TableDriver tableDriver;

    public TableManager(final TableDriver tableDriver) {
        this.tableDriver = tableDriver;
    }

    public void deleteTable(final String tableName) {
        try {
            tableDriver.deleteTable(tableName);
        } catch (ResourceNotFoundException e) {
            throw new TableNotFoundException(e.getMessage());
        }
    }

    /**
     * Creates new table.
     *
     * @param tableName The name of the table.
     * @throws InterruptedException when operation is interrupted
     */
    public void createTable(final String tableName) throws InterruptedException {
        tableDriver.createTable(tableName);     
    }

    /**
     * Fastest way to empty a table. Delete a table and create it with the same validation schema.
     * This method does not delete the validation schema of the table
     */
    public void emptyTable(final String tableName) {
        if (tableDriver.tableExists(tableName)) {
            tableDriver.deleteNoCheckTable(tableName);
            tableDriver.createTable(tableName);
        } else {
            throw new TableNotFoundException(tableName);
        }
    }

    public boolean tableExists(String tableName){
        
        return this.tableDriver.tableExists(tableName);
    }
    
    public List<String> listTables(){
        return tableDriver.listTables();
    }
}
