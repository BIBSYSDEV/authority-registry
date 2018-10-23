package no.bibsys.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.amazonaws.services.dynamodbv2.model.TableNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;

import no.bibsys.web.model.CreateRegistryRequest;

public class DatabaseManager {

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
            throw new RuntimeException(e);
        }
    }


    public void createRegistry(CreateRegistryRequest request)
            throws InterruptedException, TableAlreadyExistsException, JsonProcessingException {
        TableManager tableManager = new TableManager(tableDriver);

        String tableName = request.getRegistryName();
        String validationSchema = request.getValidationSchema();
        if (registryExists(tableName)) {
            throw new TableAlreadyExistsException(
                    String.format("Registry %s already exists", tableName));
        } else {
            tableManager.createRegistry(tableName, validationSchema);
            
            String timestamp = new Date().toString();
            String registryJson = String.format(registryJsonTemplate, tableName, timestamp);
            addEntry(TableManager.VALIDATION_SCHEMA_TABLE, registryJson, request.createAttributeMap());
        }
    }

    public void addEntry(String tableName, String json) {
        addEntry(tableName, json, new HashMap<String, Object>());
    }
    
    
    public void addEntry(String tableName, String json, Map<String, Object> attributeMap) {
        if (registryExists(tableName)) {
            TableWriter tableWriter = new TableWriter(tableDriver, tableName);
            tableWriter.addJson(json, attributeMap);
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
            
            TableWriter schemaTableWriter = new TableWriter(tableDriver, TableManager.VALIDATION_SCHEMA_TABLE);
            schemaTableWriter.deleteEntry(tableName);
            
            tableManager.deleteTable(tableName);
        } else {
            throw new TableNotFoundException(
                    String.format("Registry %s does not exist", tableName));
        }
    }

}
