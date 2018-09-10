package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;

public  class TableDriver {

  private AmazonDynamoDB client;
  private transient DynamoDB dynamoDB;


  public TableDriver(){};


  public TableDriver(final AmazonDynamoDB client, final DynamoDB dynamoDB) {
    this.client = client;
    this.dynamoDB = dynamoDB;
  }

  public AmazonDynamoDB getClient() {
    return client;
  }

  public DynamoDB getDynamoDB() {
    return dynamoDB;
  }

  public void setClient(final AmazonDynamoDB client){

    if(this.client==null){
      this.client=client;
      dynamoDB=new DynamoDB(client);
    }
    else{
      throw  new IllegalStateException("Cannot set not null client ");
    }


  }


  public Table getTable(final String tableName){
    return dynamoDB.getTable(tableName);

  }


}
