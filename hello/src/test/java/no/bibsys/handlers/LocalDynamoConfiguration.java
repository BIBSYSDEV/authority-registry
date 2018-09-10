//package no.bibsys.handlers;
//
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
//import com.amazonaws.services.dynamodbv2.document.DynamoDB;
//import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
//import no.bibsys.db.TableCreator;
//import no.bibsys.db.TableDriver;
//import no.bibsys.db.TableWriter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class LocalDynamoConfiguration {
//
//
//
////  @Bean
////  public SampleHandler getDatabaseHandler(){
////    return new SampleHandler();
////  }
//
////  @Bean
////  public SampleHandler getDatabaseHandler(TableCreator tableCreator){
////    SampleHandler databaseHandler = new SampleHandler();
////    databaseHandler.setTableCreator(tableCreator);
//////    databaseHandler.setTableWriter(writer);
////    return databaseHandler;
////
////  }
//
//
//  @Bean
//  public AmazonDynamoDB getClient() {
//    System.setProperty("java.library.path", "native-libs");
//    AmazonDynamoDB client = DynamoDBEmbedded.create().amazonDynamoDB();
//    return client;
//  }
//
//  @Bean
//  public DynamoDB getDynamoDB(AmazonDynamoDB client){
//    return new DynamoDB(client);
//  }
//
//
//
//  @Bean
//  public TableDriver tableDriver(AmazonDynamoDB client, DynamoDB dynamoDB){
//    return new TableDriver(client,dynamoDB);
//  }
//
//  @Bean
//  public TableWriter getTableWriter(TableDriver tableDriver){
//    return new TableWriter(tableDriver);
//  }
//
//
//  @Bean
//  public TableCreator getTableCreator(TableDriver tableDriver){
//    return new TableCreator(tableDriver);
//  }
//
//  @Bean
//  public String getHelloString(){
//    return "hello world";
//  }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//}
