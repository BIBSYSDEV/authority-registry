package no.bibys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

public abstract class TableDriver {


  AmazonDynamoDB client;
  DynamoDB dynamoDB;


//  protected TableDriver(){
//    this(AmazonDynamoDBClientBuilder.standard().build());
//  }


  public TableDriver(AmazonDynamoDB client){
    this.client=client;
    dynamoDB=new DynamoDB(client);
  }



}
