package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;

public class LocalDynamoTest {



  public void start(){
    AmazonDynamoDB ddb = DynamoDBEmbedded.create().amazonDynamoDB();


  }

}
