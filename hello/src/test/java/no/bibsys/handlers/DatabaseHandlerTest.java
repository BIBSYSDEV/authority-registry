package no.bibsys.handlers;

//@SpringBootTest
//@ContextConfiguration(classes={LocalDynamoConfiguration.class})
//@RunWith(SpringRunner.class)
//@DirtiesContext
//public class DatabaseHandlerTest extends LocalDynamoTest implements IOTestUtils {
//
////  @Autowired
////  private SampleHandler databaseHandler;
////
////  @Test
////  public void DatabaseHandlerShouldStoreAJsonOBjectInDatabase() throws IOException {
////    String data=resourceAsString(Paths.get("api","dbHandlerInput.json"));
////      String tableName="DatabaseHandlerTestTable";
////      databaseHandler.processInput(new DatabaseWriteRequest(tableName,data));
////    List<String> tables = databaseHandler
////        .getTableCreator().getClient().listTables().getTableNames();
////    assert(databaseHandler.getHelloString().length()>0);
//////    assertThat(tables.get(0),is(equalTo(tableName)));
////
////
////
////  }
//
//}
