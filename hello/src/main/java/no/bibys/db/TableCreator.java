package no.bibys.db;

import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import java.util.List;
import no.bibys.db.structures.Entry;

public class TableCreator extends TableDriver {


  protected TableCreator(TableDriver tableDriver) {
    super(tableDriver.getClient(), tableDriver.getDynamoDB());
  }

  public static TableCreator create(TableDriverFactory factory) {
    return new TableCreator(factory.build());
  }

  public void deleteTable(String tableName) {
    client.deleteTable(tableName);
  }

  public void createTable(String tableName, TableDefinitions tableEntry)
      throws InterruptedException {

    List<AttributeDefinition> attributeDefinitions = tableEntry
        .attributeDefinitions();
    List<KeySchemaElement> keySchema = tableEntry.keySchema();

    CreateTableRequest request = new CreateTableRequest().withTableName(tableName)
        .withKeySchema(keySchema)
        .withAttributeDefinitions(attributeDefinitions).withProvisionedThroughput(
            new ProvisionedThroughput().withReadCapacityUnits(10L).withWriteCapacityUnits(10L));

    System.out.println("Issuing CreateTable request for " + tableName);
    Table table = dynamoDB.createTable(request);

    System.out.println("Waiting for " + tableName + " to be created...this may take a while...");
    table.waitForActive();

  }


  public boolean tableExists(String tableName) {
    try {
      dynamoDB.getTable(tableName).describe().getTableStatus();
      return true;
    } catch (com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException e) {
      return false;
    }
  }

  public void createTable(String tableName) throws InterruptedException {

    Entry entry = new Entry() {
      @Override
      public String getId() {
        return null;
      }

      @Override
      public void setId(String id) {

      }
    };

    createTable(tableName, entry);
  }

}
