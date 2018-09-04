package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import java.util.ArrayList;
import no.bibys.db.TableCreator;
import no.bibys.db.TableDriverFactory;
import no.bibys.db.structures.LanguageString;
import no.bibys.db.structures.SimpleEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
public abstract class LocalDynamoTest  extends DynamoTest {



    TableDriverFactory tableDriverFactory;

  @BeforeEach
  public void init(){
    System.setProperty("java.library.path", "native-libs");
    tableDriverFactory=new TableDriverFactory();
    tableDriverFactory.setClient(DynamoDBEmbedded.create().amazonDynamoDB());
    tableCreator=TableCreator.create(tableDriverFactory);
    ArrayList<LanguageString> labels=new ArrayList<>();
    labels.add(new LanguageString("The preferred label","en"));
    entry=new SimpleEntry("TheId",labels);
  }

}
