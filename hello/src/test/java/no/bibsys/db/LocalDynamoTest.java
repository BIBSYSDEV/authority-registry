package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import java.util.ArrayList;
import no.bibys.db.TableCreator;
import no.bibys.db.structures.LanguageString;
import no.bibys.db.structures.SimpleEntry;
import org.junit.jupiter.api.BeforeEach;

public abstract class LocalDynamoTest {

   String tableName="itemTable";
   SimpleEntry entry;

   AmazonDynamoDB client;
   TableCreator tableCreator;


  @BeforeEach
  public void init(){
    System.setProperty("java.library.path", "native-libs");
    client= DynamoDBEmbedded.create().amazonDynamoDB();
    tableCreator=new TableCreator(client);
    ArrayList<LanguageString> labels=new ArrayList<>();
    labels.add(new LanguageString("The preferred label","en"));
    entry=new SimpleEntry("TheId",labels);
  }

}
