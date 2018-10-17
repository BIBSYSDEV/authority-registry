package no.bibsys.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.amazonaws.services.dynamodbv2.model.TableNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;

public class DatabaseManager {

    private static final String SCHEMA_TABLE = "SCHEMA_TABLE";
    private final transient TableDriver tableDriver;
    private transient String registryJsonTemplate = "";

    public DatabaseManager(TableDriver tableDriver) {
        this.tableDriver = tableDriver;
        
        readRegistryJsonTemplate();
    }


    private void readRegistryJsonTemplate() {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(Paths.get("json", "registry.json").toString());
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")))){
            registryJsonTemplate = String.join(" ", reader.lines().collect(Collectors.toList()));
        } catch (IOException e) {
        }
    }


    public void createRegistry(String tableName, String validationSchema)
            throws InterruptedException, TableAlreadyExistsException, JsonProcessingException {
        TableManager tableManager = new TableManager(tableDriver);

        if(!registryExists(SCHEMA_TABLE)) {
            tableManager.createRegistry(SCHEMA_TABLE, "");
        }
        
        if (registryExists(tableName)) {
            throw new TableAlreadyExistsException(
                    String.format("Registry %s already exists", tableName));
        } else {
            tableManager.createRegistry(tableName, validationSchema);
            
            String timestamp = new Date().toString();
            String registryJson = registryJsonTemplate.replace("SCHEMA_NAME", tableName);
            registryJson = registryJson.replaceAll("ID", tableName); 
            registryJson = registryJson.replace("TIMESTAMP", timestamp );
            registryJson = registryJson.replace("SCHEMA", validationSchema ); 
            
            insertEntry(SCHEMA_TABLE, registryJson );
        }
    }


    public void insertEntry(String tableName, String json) {
        if (registryExists(tableName)) {
            TableWriter tableWriter = new TableWriter(tableDriver, tableName);
            tableWriter.insertJson(json);
        } else {
            throw new TableNotFoundException(
                    String.format("Registry %s does not exist", tableName));
        }

    }

    public Optional<String> readEntry(String tableName, String id) {
        if (registryExists(tableName)) {
            TableReader tableReader = new TableReader(tableDriver, tableName);
            return tableReader.getEntry(id);
        } else {
            throw new TableNotFoundException(
                    String.format("Registry %s does not exist", tableName));
        }

    }


    public boolean registryExists(String tableName) {
        return new TableManager(tableDriver).tableExists(tableName);
    }


    public void emptyRegistry(String tableName) throws InterruptedException {
        TableManager tableManager = new TableManager(tableDriver);
        tableManager.emptyTable(tableName);

    }

    public void deleteRegistry(String tableName) throws InterruptedException {
        TableManager tableManager = new TableManager(tableDriver);
        if (registryExists(tableName)) {
            
            TableWriter schemaTableWriter = new TableWriter(tableDriver, SCHEMA_TABLE);
            schemaTableWriter.deleteEntry(tableName);
            
            tableManager.deleteTable(tableName);
        } else {
            throw new TableNotFoundException(
                    String.format("Registry %s does not exist", tableName));
        }
    }


}
