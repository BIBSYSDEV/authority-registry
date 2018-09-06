package no.bibsys.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import no.bibys.handlers.DatabaseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@ComponentScan("no.bibsys.db")
public class LocalDynamoConfiguration {



  @Bean
  public AmazonDynamoDB getClient() {
    System.setProperty("java.library.path", "native-libs");
    AmazonDynamoDB client = DynamoDBEmbedded.create().amazonDynamoDB();
    return client;
  }

  @Bean
  public DynamoDB getDynamo(AmazonDynamoDB client) {
    return  new DynamoDB(client);
  }



  @Bean
  public DatabaseHandler getDatabaseHandler(){
    return new DatabaseHandler();
  }



}
