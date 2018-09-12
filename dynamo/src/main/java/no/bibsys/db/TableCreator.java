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


  private static Logger logger = LoggerFactory.getLogger(TableCreator.class);

  final transient private TableDriver tableDriver;

  public TableCreator(final TableDriver tableDriver) {
    this.tableDriver = tableDriver;
  }

  public void deleteTable(final String tableName) {
    tableDriver.getClient().deleteTable(tableName);
  }


  AmazonDynamoDB getClient() {
    return tableDriver.getClient();
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

    System.out.println("Issuing CreateTable request for " + tableName);
    final Table table = tableDriver.getDynamoDB().createTable(request);

    System.out.println("Waiting for " + tableName + " to be created...this may take a while...");
    table.waitForActive();

  }


  public boolean tableExists(final String tableName) {
    boolean exists = false;
    try {
      exists = tableDriver.getTable(tableName).describe().getTableStatus() != null;
    } catch (com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException e) {
      logger.warn("Table {} does not exist", tableName);
    }
    return exists;
  }

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

}
