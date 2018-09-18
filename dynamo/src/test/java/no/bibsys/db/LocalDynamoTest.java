package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import org.junit.Before;


public abstract class LocalDynamoTest extends DynamoTest {


  protected AmazonDynamoDB localClient;

  @Before
  public void init() {
    System.setProperty("java.library.path", "native-libs");
    localClient = DynamoDBEmbedded.create().amazonDynamoDB();
  }


}
