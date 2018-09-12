package no.bibsys;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import no.bibsys.db.TableCreator;
import no.bibsys.db.TableDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultConfiguration {


  @Bean
  public TableCreator getTableCreator(TableDriver tableDriver) {
    return new TableCreator(tableDriver);
  }


  @Bean
  public TableDriver getTableDriver() {
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    DynamoDB dynamoDb = new DynamoDB(client);
    return new TableDriver(client, dynamoDb);
  }

}
