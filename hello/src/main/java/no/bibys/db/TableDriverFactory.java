package no.bibys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

public class TableDriverFactory {

  private  DynamoDB dynamoDB;
  private  AmazonDynamoDB client;


  public TableDriver build(){
    init();
    return new TableDriver(client,dynamoDB);
  }

  private void init(){
    if(client==null)
      this.client= AmazonDynamoDBClientBuilder.standard().build();

    this.dynamoDB=new DynamoDB(client);
  }


  public void setClient(AmazonDynamoDB client) {
    this.client = client;
  }

}
