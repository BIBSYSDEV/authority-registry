package no.bibsys.db;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import com.amazonaws.services.dynamodbv2.document.Item;
import java.io.IOException;
import java.nio.file.Paths;
import no.bibsys.utils.IOTestUtils;
import no.bibys.db.TableReader;
import no.bibys.db.TableWriter;
import org.junit.jupiter.api.Test;

public class TableWriterTest extends  LocalDynamoTest implements IOTestUtils {



  @Test
  public void insertJson() throws IOException, InterruptedException {

    String json=resourceAsString(Paths.get("db","sample.json"));
    Item inputItem= Item.fromJSON(json);
    tableCreator.createTable(tableName,entry);

    TableWriter entryManager=new TableWriter(tableName,client);
    entryManager.insertJson(json);
    TableReader tableReader=new TableReader(tableName,client);
    String output=tableReader.getEntry("id01");
    Item outputItem=Item.fromJSON(output.toUpperCase());
    assertThat(outputItem, is(equalTo(inputItem)));

  }


}
