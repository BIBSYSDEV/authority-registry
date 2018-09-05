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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TableCreator {

  private TableDriver tableDriver;

  @Autowired
  public TableCreator(TableDriver tableDriver) {
    this.tableDriver = tableDriver;
  }

  public void deleteTable(String tableName) {
    tableDriver.getClient().deleteTable(tableName);
  }


  public AmazonDynamoDB getClient(){
    return tableDriver.getClient();
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
    Table table = tableDriver.getDynamoDB().createTable(request);

    System.out.println("Waiting for " + tableName + " to be created...this may take a while...");
    table.waitForActive();

  }


  public boolean tableExists(String tableName) {
    try {
      tableDriver.getTable(tableName).describe().getTableStatus();
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
