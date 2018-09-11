package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import org.junit.Before;


public abstract class LocalDynamoTest{



  AmazonDynamoDB client;

  @Before
  public void init(){
    System.setProperty("java.library.path", "native-libs");
    client= DynamoDBEmbedded.create().amazonDynamoDB();
  }


}
