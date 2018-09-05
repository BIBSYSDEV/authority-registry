package no.bibys;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamoDBConfiguration {

    @Bean
    public AmazonDynamoDB getClient() {
      return AmazonDynamoDBClientBuilder.standard().build();
    }

    @Bean
    public DynamoDB getDynamo(AmazonDynamoDB client) {
      return  new DynamoDB(client);
    }

}
