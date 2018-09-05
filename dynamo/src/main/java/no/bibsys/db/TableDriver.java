package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public  class TableDriver {

  private AmazonDynamoDB client;
  private DynamoDB dynamoDB;


  @Autowired
  public TableDriver(AmazonDynamoDB client, DynamoDB dynamoDB) {
    this.client = client;
    this.dynamoDB = dynamoDB;
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
