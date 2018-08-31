package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import java.util.ArrayList;
import no.bibsys.utils.IOTestUtils;
import no.bibys.db.TableCreator;
import no.bibys.db.structures.LanguageString;
import no.bibys.db.structures.SimpleEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TableCreatorTest implements IOTestUtils {


  String tableName="itemTable";
  SimpleEntry entry;
  AmazonDynamoDB client= DynamoDBEmbedded.create().amazonDynamoDB();

  TableCreator tableCreator=new TableCreator(client);

  @BeforeEach
  public void init(){
    ArrayList<LanguageString> labels=new ArrayList<>();
    labels.add(new LanguageString("The preferred label","en"));
    entry=new SimpleEntry("DasID",labels);
  }




  @Test
  public void createTable() throws InterruptedException {
    tableCreator.createTable(tableName, entry);

  }


//  @Test
//  public void insertItem() throws JsonProcessingException {
//   init();
//    TableWriter entryManager=new TableWriter("itemTable");
//    entryManager.insertEntry(entry);
//
//  }
//
//
//
//
//  @Test
//  public void insertJson() throws IOException {
//    String json=resourceAsString(Paths.get("db","sample.json"));
//    Item inputItem= Item.fromJSON(json);
//    TableWriter entryManager=new TableWriter(tableName);
//    entryManager.insertJson(json);
//    TableReader tableReader=new TableReader(tableName);
//    String output=tableReader.getEntry("id01");
//    Item outputItem=Item.fromJSON(output);
//    assertThat(outputItem, is(equalTo(inputItem)));
//
//  }
//
//
//
//
//
//  @Test
//  public void getItem() throws IOException {
//    insertJson();
//    TableReader entryManager=new TableReader(tableName);
//    String json=entryManager.getEntry("id01");
//    System.out.println(json);
//
//  }

}
