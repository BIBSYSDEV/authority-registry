package no.bibsys;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import no.bibsys.db.TableDriver;


public class LocalDynamoDBHelper {

    private LocalDynamoDBHelper() {
        // TODO Auto-generated constructor stub
    }

    public static TableDriver getTableDriver() {
        System.setProperty("java.library.path", "native-libs");
        AmazonDynamoDB client = DynamoDBEmbedded.create().amazonDynamoDB();

        DynamoDB dynamoDb = new DynamoDB(client);
        return TableDriver.create(client, dynamoDb);
    }

}
