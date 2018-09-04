package no.bibys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import org.springframework.beans.factory.annotation.Autowired;

public  class TableDriver {

  protected AmazonDynamoDB client;
  protected DynamoDB dynamoDB;


  @Autowired
  protected TableDriver(AmazonDynamoDB client, DynamoDB dynamoDB){
    this.client= client;
    this.dynamoDB=dynamoDB;
  }

  public AmazonDynamoDB getClient() {
    return client;
  }


  public void setClient(AmazonDynamoDB client) {
    this.client = client;
  }

  public DynamoDB getDynamoDB() {
    return dynamoDB;
  }

  public void setDynamoDB(DynamoDB dynamoDB) {
    this.dynamoDB = dynamoDB;
  }


  public Table getTable(String tableName){
    return dynamoDB.getTable(tableName);

  }


}
