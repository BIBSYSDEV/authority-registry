package no.bibys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import java.util.List;

public class TableCreator  extends TableDriver{

  public TableCreator(){
     super(AmazonDynamoDBClientBuilder.standard().build());
  }

  public TableCreator(AmazonDynamoDB client){
    super(client);
  }


  public void deleteTable(String tableName){

    client.deleteTable(tableName);
  }

  public void createTable(String tableName, TableDefinitions tableEntry) throws InterruptedException {

    List<AttributeDefinition> attributeDefinitions = tableEntry
        .attributeDefinitions();
    List<KeySchemaElement> keySchema = tableEntry.keySchema();

    CreateTableRequest request = new CreateTableRequest().withTableName(tableName).withKeySchema(keySchema)
        .withAttributeDefinitions(attributeDefinitions).withProvisionedThroughput(
            new ProvisionedThroughput().withReadCapacityUnits(10L).withWriteCapacityUnits(10L));

    System.out.println("Issuing CreateTable request for " + tableName);
    Table table = dynamoDB.createTable(request);

    System.out.println("Waiting for " + tableName + " to be created...this may take a while...");
    table.waitForActive();





  }

}
