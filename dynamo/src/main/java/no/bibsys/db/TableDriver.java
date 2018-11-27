package no.bibsys.db;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.Select;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.TableUtils;

import no.bibsys.db.structures.IdOnlyEntry;
import no.bibsys.db.structures.TableDefinitions;

public final class TableDriver {

    private static final Logger logger = LoggerFactory.getLogger(TableDriver.class);
    private transient AmazonDynamoDB client;
    private transient DynamoDB dynamoDb;


    private TableDriver() {}

    private TableDriver(final AmazonDynamoDB client) {
        this.client = client;
        this.dynamoDb = new DynamoDB(client);
    }

    /**
     * Create custom connection with DynamoDB.
     *
     * @return customized TableDriver
     */
    public static TableDriver create(final AmazonDynamoDB client) {
        if (client == null) {
            throw new IllegalStateException("Cannot set null client ");
        }
        TableDriver tableDriver = new TableDriver(client);
        return tableDriver;
    }

    public Table getTable(final String tableName) {
        return dynamoDb.getTable(tableName);
    }


    /**
     * Check if table exists.
     *
     * @param tableName The name of the table.
     * @return true if table exists, false otherwise
     */
    public boolean tableExists(final String tableName) {
        boolean exists = false;
        try {
            TableDescription describe = getTable(tableName).describe();
            String tableStatus = describe.getTableStatus();
            exists = tableStatus != null;
        } catch (ResourceNotFoundException e) {
            logger.debug("Table {} does not exist", tableName);
        }
        return exists;
    }

    /**
     * Return number of items in table
     * 
     * @param tableName
     * @return number of items
     */

    public long tableSize(final String tableName) {

        ScanRequest scanRequest = new ScanRequest(tableName).withSelect(Select.COUNT);
        Integer itemCount = 0;
        ScanResult result = null;
        do {
            if (result != null) {
                scanRequest.setExclusiveStartKey(result.getLastEvaluatedKey());
            }

            result = client.scan(scanRequest);
            itemCount += result.getScannedCount();
        } while (result.getLastEvaluatedKey() != null);
        logger.info("Table has {} items, tableId={}", itemCount, tableName);
        return itemCount;
    }

    public boolean emptyTable(final String tableName) {

        if (!tableExists(tableName)) {
            return false;
        }
        boolean emptyResult = deleteNoCheckTable(tableName);
        return emptyResult && createTable(tableName);
    }

    public boolean deleteTable(final String tableName) {

        if (!tableExists(tableName)) {
            return false;
        }

        if (isEmpty(tableName)) {
            return deleteNoCheckTable(tableName);
        } else {
            return false;
        }
    }

    private boolean isEmpty(final String tableName) {
        return tableSize(tableName) == 0;
    }

    private boolean deleteNoCheckTable(final String tableName) {
        if (tableExists(tableName)) {
                
            DeleteTableRequest deleteRequest = new DeleteTableRequest(tableName);
            TableUtils.deleteTableIfExists(client, deleteRequest);
            logger.debug("Table deleted successfully, tableId={}", tableName);
            return true;
        }
        logger.error("Can not delete non-existing table, tableId={}", tableName);
        return false;

    }

    public boolean createTable(final String tableName, final TableDefinitions tableEntry) {

        if (!tableExists(tableName)) {
            final List<AttributeDefinition> attributeDefinitions =
                    tableEntry.attributeDefinitions();
            final List<KeySchemaElement> keySchema = tableEntry.keySchema();

            final CreateTableRequest request = new CreateTableRequest().withTableName(tableName)
                    .withKeySchema(keySchema).withAttributeDefinitions(attributeDefinitions)
                    .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L)
                            .withWriteCapacityUnits(1L));

            TableUtils.createTableIfNotExists(client, request);
            logger.debug("Table created, tableId={}", tableName);
            return true;
        }
        logger.error("Tried to create table but it already exists, tableId={}", tableName);
        return false;
    }


    public boolean createTable(final String tableName) {

        return createTable(tableName, new IdOnlyEntry());
    }

    public List<String> listTables() {
        List<String> tableList = new ArrayList<>();
        dynamoDb.listTables().forEach(table -> tableList.add(table.getTableName()));
        logger.info("Listing {} tables", tableList.size());
        return tableList;
    }

    public String status(String tableName) {
        
        try {
            TableDescription describe = getTable(tableName).describe();
            return describe.getTableStatus();
        }catch(ResourceNotFoundException e) {
            return "NOT_FOUND";
        }
    }
}
