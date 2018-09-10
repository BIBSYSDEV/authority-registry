package no.bibsys.db;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import java.io.IOException;
import java.nio.file.Paths;
import no.bibsys.utils.IoTestUtils;
import org.junit.Before;
import org.junit.Test;


public class TableWriterAndReaderTest extends LocalDynamoTest implements IoTestUtils {


  TableReader tableReader;
  TableWriter tableWriter;
  TableCreator tableCreator;


  @Override
  @Before
  public void init() {
    super.init();
    tableReader = new TableReader(new TableDriver(localClient, new DynamoDB(localClient)));
    tableWriter = new TableWriter(new TableDriver(localClient, new DynamoDB(localClient)));
    tableCreator = new TableCreator(new TableDriver(localClient, new DynamoDB(localClient)));
    tableReader.setTableName(tableName);
    tableWriter.setTableName(tableName);
  }


  @Test
  public void insertJson() throws IOException, InterruptedException {

    String json = resourceAsString(Paths.get("json", "sample.json"));
    Item inputItem = Item.fromJSON(json);
    tableCreator.createTable(tableName, entry);
    tableWriter.insertJson(json);
    String output = tableReader.getEntry("id01");
    Item outputItem = Item.fromJSON(output);
    assertThat(outputItem, is(equalTo(inputItem)));

  }


}
