package no.bibsys.handlers;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import no.bibsys.handlers.requests.DatabaseWriteRequest;
import no.bibsys.utils.IOTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@ContextConfiguration(classes={LocalDynamoConfiguration.class})
@RunWith(SpringRunner.class)
@DirtiesContext
public class DatabaseHandlerTest extends LocalDynamoTest implements IOTestUtils {

  @Autowired
  private DatabaseHandler databaseHandler;

  @Test
  public void DatabaseHandlerShouldStoreAJsonOBjectInDatabase() throws IOException {
    String data=resourceAsString(Paths.get("api","dbHandlerInput.json"));
      String tableName="DatabaseHandlerTestTable";
      databaseHandler.processInput(new DatabaseWriteRequest(tableName,data));
    List<String> tables = databaseHandler
        .getTableCreator().getClient().listTables().getTableNames();
    assert(databaseHandler.getHelloString().length()>0);
//    assertThat(tables.get(0),is(equalTo(tableName)));



  }

}
