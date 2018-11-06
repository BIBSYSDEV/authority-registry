package no.bibsys;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import no.bibsys.db.TableDriver;


public class LocalDynamoDBHelper {

    private LocalDynamoDBHelper() {
        // TODO Auto-generated constructor stub
    }

    private static TableDriver tableDriver = null;
    public static TableDriver getTableDriver() {
        
        System.setProperty("java.library.path", "native-libs");
        if(tableDriver == null) {
            AmazonDynamoDB client = DynamoDBEmbedded.create().amazonDynamoDB();

            DynamoDB dynamoDb = new DynamoDB(client);
            tableDriver = TableDriver.create(client, dynamoDb);
        }
        return tableDriver; 
    }

}
