package no.bibsys;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import no.bibsys.db.TableDriver;
import org.springframework.context.annotation.Bean;


/**
 * Default Configuration for the SpringBoot application.
 */
public class DefaultConfiguration {


    /**
     * Creates a TableDriver.
     *
     * @return a new TableDriver.
     */
    @Bean
    public TableDriver getTableDriver() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        DynamoDB dynamoDb = new DynamoDB(client);
        return TableDriver.create(client, dynamoDb);
    }

}
