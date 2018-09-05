package no.bibsys.db;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
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
public class TableCreatorTest extends LocalDynamoTest implements IOTestUtils {

  @Autowired
  private TableCreator tableCreator;



  @Test
  public void createTable() throws InterruptedException {
    tableCreator.createTable(tableName, entry);
    ListTablesResult tables = tableCreator.getClient().listTables();
    int numberOftables = tables.getTableNames().size();
    assertThat(numberOftables, is(equalTo(1)));


  }

}
