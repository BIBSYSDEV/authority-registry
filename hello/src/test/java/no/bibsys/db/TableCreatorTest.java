package no.bibsys.db;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.Test;

public class TableCreatorTest {


//  String tableName="itemTable";
//  SimpleEntry entry;
//  AmazonDynamoDB client= DynamoDBEmbedded.create().amazonDynamoDB();

//  TableCreator tableCreator=new TableCreator(client);

//  @BeforeEach
//  public void init(){
//    ArrayList<LanguageString> labels=new ArrayList<>();
//    labels.add(new LanguageString("The preferred label","en"));
//    entry=new SimpleEntry("DasID",labels);
//  }



  @Test
  public void testTest(){
    assertThat(2, is(equalTo(1)));
  }

//  @Test
//  public void createTable() throws InterruptedException {
//    tableCreator.createTable(tableName, entry);
//    throw new RuntimeException("AAAAAAAAAAAAAa");
//
//  }


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
