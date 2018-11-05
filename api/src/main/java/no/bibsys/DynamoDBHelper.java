package no.bibsys;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import no.bibsys.db.TableDriver;


public final class DynamoDBHelper {

    private DynamoDBHelper() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Creates a TableDriver.
     *
     * @return a new TableDriver.
     */
    public static TableDriver getTableDriver() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        DynamoDB dynamoDb = new DynamoDB(client);
        return TableDriver.create(client, dynamoDb);
    }
    
    public static AmazonDynamoDB getAmazonDynamoDB() {
        return AmazonDynamoDBClientBuilder.standard().build();
    }

}
