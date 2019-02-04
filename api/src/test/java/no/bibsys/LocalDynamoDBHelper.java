package no.bibsys;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;


public class LocalDynamoDBHelper {

    private LocalDynamoDBHelper() {
    }

    public static AmazonDynamoDB getClient() {
        System.setProperty("java.library.path", "native-libs");
        return DynamoDBEmbedded.create().amazonDynamoDB();
    }
}
