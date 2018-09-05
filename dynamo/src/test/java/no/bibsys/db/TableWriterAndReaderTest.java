package no.bibsys.db;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import com.amazonaws.services.dynamodbv2.document.Item;
import java.io.IOException;
import java.nio.file.Paths;
import no.bibsys.utils.IOTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {LocalDynamoConfiguration.class})
@DirtiesContext
public class TableWriterAndReaderTest extends LocalDynamoTest implements IOTestUtils {


  @Autowired
  TableReader tableReader;

  @Autowired
  TableWriter tableWriter;


  @Autowired
  TableCreator tableCreator;




  @Test
  public void insertJson() throws IOException, InterruptedException {


    String json=resourceAsString(Paths.get("json","sample.json"));
    Item inputItem= Item.fromJSON(json);
    tableCreator.createTable(tableName,entry);
    tableWriter.setTableName(tableName);
    tableWriter.insertJson(json);
    tableReader.setTableName(tableName);
    String output=tableReader.getEntry("id01");
    Item outputItem=Item.fromJSON(output);
    assertThat(outputItem, is(equalTo(inputItem)));

  }


}
