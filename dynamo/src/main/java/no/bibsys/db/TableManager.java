package no.bibsys.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.amazonaws.services.dynamodbv2.model.TableNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;


public class TableManager {

    public static final String VALIDATION_SCHEMA_TABLE = "VALIDATION_SCHEMA_TABLE";
    private final transient TableDriver tableDriver;
    private final static transient Logger logger = LoggerFactory.getLogger(TableManager.class);


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
        if (tableDriver.tableExists(tableName)) {
            tableDriver.deleteNoCheckTable(tableName);
            tableDriver.createTable(tableName);
        } else {
            throw new TableNotFoundException(tableName);
        }
    }

    public void createRegistry(String tableName, String validationSchema)
        throws InterruptedException, JsonProcessingException {
        
        if(!tableExists(VALIDATION_SCHEMA_TABLE)) {
            tableDriver.createTable(VALIDATION_SCHEMA_TABLE);
        }
        
        if(!tableExists(tableName)) {
            TableWriter writer = new TableWriter(tableDriver, VALIDATION_SCHEMA_TABLE);
            Path path = Paths.get("json", "registry.json");
            String json = "";
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(path.toString()), StandardCharsets.UTF_8))) {
                json = String.join(" ", reader.lines().collect(Collectors.toList()));
            } catch (IOException e) {
                logger.error("Unable to read registry.json");
            }
            
            json = json.replaceAll("TABLENAME", tableName);
            
            writer.addJson(json);
            
            tableDriver.createTable(tableName);
        }else {
            throw new TableAlreadyExistsException(String.format("Table %s allready exists", tableName));
        }

    }

    public boolean tableExists(String tableName){
        return this.tableDriver.tableExists(tableName);
    }


}
