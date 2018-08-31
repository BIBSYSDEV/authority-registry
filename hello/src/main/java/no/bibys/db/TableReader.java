package no.bibys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonProcessingException;

public class TableReader extends TableDriver{

  private String tableName;

  public TableReader(String tableName) {
    this(tableName,AmazonDynamoDBClientBuilder.standard().build());
  }


  public TableReader(String tableName, AmazonDynamoDB client) {
    super(client);
    this.tableName = tableName;

  }


  public String getEntry(String id) throws JsonProcessingException {
    Table table = dynamoDB.getTable(tableName);
    Item item=table.getItem("id",id);
    String json=item.toJSON();
    return json;
  }

}
