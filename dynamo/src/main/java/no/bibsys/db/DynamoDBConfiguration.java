package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class DynamoDBConfiguration {

    @Bean
    public AmazonDynamoDB getClient() {
      return AmazonDynamoDBClientBuilder.standard().build();
    }

    @Bean
    public DynamoDB getDynamo(AmazonDynamoDB client) {
      return  new DynamoDB(client);
    }

    @Bean
    public TableCreator getTableCreator(TableDriver tableDriver){
      return new TableCreator(tableDriver);
    }

}
