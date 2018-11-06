package no.bibsys.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
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
            EntityManager writer = new EntityManager(tableDriver, getValidationSchemaTable());
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
            EntityManager writer = new EntityManager(tableDriver, getValidationSchemaTable());

            writer.addJson(objectMapper.writeValueAsString(template));

            tableDriver.createTable(tableName);     
        }else {
            throw new TableAlreadyExistsException(String.format("Table %s already exists", tableName));
        }

    }

    public boolean tableExists(String tableName){
        
        return this.tableDriver.tableExists(tableName);
    }

    public boolean registryExists(String tableName) throws InterruptedException{
        

        if(!tableExists(getValidationSchemaTable())) {
            tableDriver.createTable(getValidationSchemaTable());
        }

        EntityManager entityManager = new EntityManager(tableDriver, getValidationSchemaTable());
        
        boolean tableExists = this.tableDriver.tableExists(tableName);
        boolean present = entityManager.getEntry(tableName).isPresent();
        return tableExists&&present;
    }
    
    public static String getValidationSchemaTable() {
        String validationSchemaTableName = "VALIDATION_SCHEMA_TABLE";
        String stage = System.getenv("STAGE_NAME");
        if("test".equals(stage)) {
            validationSchemaTableName = String.join("_", "TEST", validationSchemaTableName);
        }

        return validationSchemaTableName;
    }

    public List<String> listAllTables(){
        TableCollection<ListTablesResult> tables = tableDriver.getDynamoDb().listTables();
        List<String> tableList = new ArrayList<>();
        tables.forEach(table -> tableList.add(table.getTableName()));
        
        return tableList;
    }
    
    public List<String> listRegistries() {
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(getValidationSchemaTable());

        ScanResult scanResult = null;
        List<String> registryNameList = new ArrayList<>(); 
        do {
            if(scanResult != null) {
                scanRequest.setExclusiveStartKey(scanResult.getLastEvaluatedKey());
            }

            scanResult = tableDriver.getClient()
                    .scan(scanRequest);
            List<Map<String,AttributeValue>> items = scanResult
                    .getItems();
            registryNameList.addAll(items.stream()
                    .map(entry -> entry.get("id").getS())
                    .collect(Collectors.toList()));
            
        } while (scanResult.getLastEvaluatedKey() != null);

        return registryNameList;
    }

    public void updateRegistryMetadata(EntityRegistryTemplate request) throws TableNotFoundException, JsonProcessingException{

        if(!tableExists(request.getId())) {
            throw new TableNotFoundException(String.format("No registry with name %s exists", request.getId()));
        }

        ObjectMapper mapper = new ObjectMapper();
        
        EntityManager entityManager = new EntityManager(tableDriver, getValidationSchemaTable());
        entityManager.updateJson(mapper.writeValueAsString(request));
        
    }

}
