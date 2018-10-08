package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import java.util.List;
import no.bibsys.db.structures.Entry;
import no.bibsys.db.structures.TableDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TableCreator {


    private final static Logger logger = LoggerFactory.getLogger(TableCreator.class);

    private final transient TableDriver tableDriver;


    public TableCreator(final TableDriver tableDriver) {
        this.tableDriver = tableDriver;
    }

    public void deleteTable(final String tableName) {
        tableDriver.deleteTable(tableName);
    }


    public AmazonDynamoDB getClient() {
        return tableDriver.getClient();
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
            exists = tableDriver.getTable(tableName).describe().getTableStatus() != null;
        } catch (com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException e) {
            logger.warn("Table {} does not exist", tableName);
        }
        return exists;
    }


    /**
     * Creates new table.
     *
     * @param tableName The name of the table.
     * @throws InterruptedException when operation is interrupted
     */
    public void createTable(final String tableName) throws InterruptedException {

        final Entry entry = new Entry() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public void setId(final String id) {

            }
        };

        createTable(tableName, entry);
    }

    private void createTable(final String tableName, final TableDefinitions tableEntry)
        throws InterruptedException {

        final List<AttributeDefinition> attributeDefinitions = tableEntry
            .attributeDefinitions();
        final List<KeySchemaElement> keySchema = tableEntry.keySchema();

        final CreateTableRequest request = new CreateTableRequest().withTableName(tableName)
            .withKeySchema(keySchema)
            .withAttributeDefinitions(attributeDefinitions).withProvisionedThroughput(
                new ProvisionedThroughput().withReadCapacityUnits(10L).withWriteCapacityUnits(10L));

        final Table table = tableDriver.getDynamoDb().createTable(request);
        table.waitForActive();

    }

}
