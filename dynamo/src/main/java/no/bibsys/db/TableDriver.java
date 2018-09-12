package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;

public class TableDriver {

  private AmazonDynamoDB client;
  private transient DynamoDB dynamoDB;


  public TableDriver() {
  }


  public static TableDriver create() {
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    DynamoDB dynamoDB = new DynamoDB(client);
    return new TableDriver(client, dynamoDB);
  }


  public TableDriver(final AmazonDynamoDB client, final DynamoDB dynamoDB) {
    this.client = client;
    this.dynamoDB = dynamoDB;
  }

  public AmazonDynamoDB getClient() {
    return client;
  }

  public void setClient(final AmazonDynamoDB client) {

    if (this.client == null) {
      this.client = client;
      dynamoDB = new DynamoDB(client);
    } else {
      throw new IllegalStateException("Cannot set not null client ");
    }


  }

  public DynamoDB getDynamoDB() {
    return dynamoDB;
  }

  public Table getTable(final String tableName) {
    return dynamoDB.getTable(tableName);

  }


}
