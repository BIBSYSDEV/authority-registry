package no.bibys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;

public  class TableDriver {

  protected AmazonDynamoDB client;
  protected DynamoDB dynamoDB;

  protected TableDriver(AmazonDynamoDB client, DynamoDB dynamoDB){
    this.client= client;
    this.dynamoDB=dynamoDB;
  }

  public AmazonDynamoDB getClient() {
    return client;
  }

  public DynamoDB getDynamoDB() {
    return dynamoDB;
  }



  public Table getTable(String tableName){
    return dynamoDB.getTable(tableName);

  }


}
