package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import java.util.ArrayList;
import no.bibsys.db.structures.LanguageString;
import no.bibsys.db.structures.SimpleEntry;
import org.junit.Before;


public abstract class LocalDynamoTest extends DynamoTest {


  protected AmazonDynamoDB localClient;

  @Before
  public void init() {
    System.setProperty("java.library.path", "native-libs");
    localClient = DynamoDBEmbedded.create().amazonDynamoDB();
    ArrayList<LanguageString> labels = new ArrayList<>();
    labels.add(new LanguageString("The preferred label", "en"));
    entry = new SimpleEntry("TheId", labels);
  }


}
