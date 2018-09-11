package no.bibsys.db;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import no.bibsys.utils.IOTestUtils;
import org.junit.Test;


public class TableCreatorTest extends LocalDynamoTest implements IOTestUtils {

  @Test
  public void createTable() throws InterruptedException {
    TableDriver tableDriver=new TableDriver();
    tableDriver.setClient(localClient);
    TableCreator tableCreator=new TableCreator(tableDriver);
    tableCreator.createTable(tableName, entry);
    ListTablesResult tables = tableCreator.getClient().listTables();
    int numberOftables = tables.getTableNames().size();
    assertThat(numberOftables, is(equalTo(1)));


  }

}
