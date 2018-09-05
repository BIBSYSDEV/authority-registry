package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.document.Item;
import java.io.IOException;
import java.nio.file.Paths;
import no.bibsys.utils.IOTestUtils;

public class TableWriterAndReaderTest extends  LocalDynamoTest implements IOTestUtils {




  public void insertJson() throws IOException, InterruptedException {

    String json=resourceAsString(Paths.get("no/bibsys/db","sample.json"));
    Item inputItem= Item.fromJSON(json);
    tableCreator.createTable(tableName,entry);
//    TableWriter entryManager=TableWriter.create(tableName,tableDriverFactory);
//    entryManager.insertJson(json);
//    TableReader tableReader=TableReader.create(tableName,tableDriverFactory);
//    String output=tableReader.getEntry("id01");
//    Item outputItem=Item.fromJSON(output);
//    assertThat(outputItem, is(equalTo(inputItem)));

  }


}
