package no.bibsys;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;


public final class DynamoDBHelper {

    private DynamoDBHelper() {
        // TODO Auto-generated constructor stub
    }
    
    public static AmazonDynamoDB getClient() {
        return AmazonDynamoDBClientBuilder.standard().build();
    }

}
