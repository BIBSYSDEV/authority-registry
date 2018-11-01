package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.TableNotFoundException;
import com.amazonaws.services.dynamodbv2.util.TableUtils;

import java.util.List;
import no.bibsys.db.exceptions.TableNotEmptyException;
import no.bibsys.db.structures.IdOnlyEntry;
import no.bibsys.db.structures.TableDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TableDriver {

    private static final Logger logger = LoggerFactory.getLogger(TableDriver.class);
    private transient AmazonDynamoDB client;
    private transient DynamoDB dynamoDb;


    private TableDriver() {
    }

    private TableDriver(final AmazonDynamoDB client, final DynamoDB dynamoDb) {
        this.client = client;
        this.dynamoDb = dynamoDb;
    }


    /**
     * Create custom connection with DynamoDB.
     *
     * @return customized TableDriver
     */
    public static TableDriver create(final AmazonDynamoDB client, final DynamoDB dynamoDb) {
        if (client == null) {
            throw new IllegalStateException("Cannot set null client ");
        }
        TableDriver tableDriver = new TableDriver(client, dynamoDb);
        return tableDriver;
    }

    public AmazonDynamoDB getClient() {
        return client;
    }

    public DynamoDB getDynamoDb() {
        return dynamoDb;
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
            exists = getTable(tableName).describe().getTableStatus() != null;
        } catch (com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException e) {
            logger.warn("Table {} does not exist", tableName);
        }
        return exists;
    }


    /**
     * Deletes a table and the validation schema associated with this table
     */
    public void deleteTable(final String tableName) throws InterruptedException {
        long itemCount = dynamoDb.getTable(tableName).describe().getItemCount();
        if (itemCount == 0) {
            deleteNoCheckTable(tableName);
        } else {
            throw new TableNotEmptyException(tableName);
        }

    }


//    /**
//     *  Deletes table metadata and validation schema
//     */
//    private void deleteValidationSchema(String tableName) {
//        
//        logger.info("Deleting validation schema for table {}", tableName);
//    }

    public  void deleteNoCheckTable(final String tableName) throws InterruptedException {
        if (tableExists(tableName)) {
            client.deleteTable(tableName);
            if (tableExists(tableName)) {
                DeleteTableRequest deleteRequest = new DeleteTableRequest();
                deleteRequest.setTableName(tableName);
                
                TableUtils.deleteTableIfExists(client, deleteRequest);
                
//                dynamoDb.getTable(tableName).waitForDelete();
            }
        } else {
            throw new TableNotFoundException(tableName);
        }

    }

    public void createTable(final String tableName, final TableDefinitions tableEntry)
        throws InterruptedException {

        final List<AttributeDefinition> attributeDefinitions = tableEntry
            .attributeDefinitions();
        final List<KeySchemaElement> keySchema = tableEntry.keySchema();

        final CreateTableRequest request = new CreateTableRequest().withTableName(tableName)
            .withKeySchema(keySchema)
            .withAttributeDefinitions(attributeDefinitions).withProvisionedThroughput(
                new ProvisionedThroughput().withReadCapacityUnits(10L).withWriteCapacityUnits(10L));

        dynamoDb.createTable(request);
    }


    public void createTable(final String tableName) throws InterruptedException {

        createTable(tableName, new IdOnlyEntry());
    }

    public boolean isTableCreated(final String tableName) {
        Table table = dynamoDb.getTable(tableName);
        
        return table != null;
    }
}
