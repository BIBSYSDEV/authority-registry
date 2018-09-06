package no.bibsys.handlers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import no.bibsys.utils.IOTestUtils;
import no.bibys.HandlerConfiguration;
import no.bibys.handlers.DatabaseHandler;
import no.bibys.handlers.requests.DatabaseWriteRequest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@ContextConfiguration(classes={HandlerConfiguration.class,LocalDynamoConfiguration.class})
@RunWith(SpringRunner.class)
@DirtiesContext
public class DatabaseHandlerTest extends LocalDynamoTest implements IOTestUtils {

  @Autowired
  private DatabaseHandler databaseHandler;

  @Test
  @Ignore
  public void DatabaseHandlerShouldStoreAJsonOBjectInDatabase() throws IOException {
    String data=resourceAsString(Paths.get("api","dbHandlerInput.json"));
      String tableName="DatabaseHandlerTestTable";
      databaseHandler.processInput(new DatabaseWriteRequest(tableName,data));
    List<String> tables = databaseHandler
        .getTableCreator().getClient().listTables().getTableNames();
    assertThat(tables.get(0),is(equalTo(tableName)));

  }

}
