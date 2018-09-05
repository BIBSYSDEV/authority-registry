package no.bibsys.db;

import java.util.ArrayList;
import no.bibsys.db.structures.LanguageString;
import no.bibsys.db.structures.SimpleEntry;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public abstract class LocalDynamoTest  extends DynamoTest {



  @Before
  public void init(){
    System.setProperty("java.library.path", "native-libs");
    ArrayList<LanguageString> labels=new ArrayList<>();
    labels.add(new LanguageString("The preferred label","en"));
    entry=new SimpleEntry("TheId",labels);
  }


}
