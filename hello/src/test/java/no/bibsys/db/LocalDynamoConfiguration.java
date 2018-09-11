package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import org.springframework.context.annotation.Bean;

public class LocalDynamoConfiguration {



  @Bean
  public TableDriver getTableDriver(){
    System.setProperty("java.library.path", "native-libs");
    AmazonDynamoDB client = DynamoDBEmbedded.create()
        .amazonDynamoDB();

    DynamoDB dynamoDB=new DynamoDB(client);
    return new TableDriver(client,dynamoDB);
  }

}
