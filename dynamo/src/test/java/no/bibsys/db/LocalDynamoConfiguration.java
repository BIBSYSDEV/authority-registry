package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
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




}
