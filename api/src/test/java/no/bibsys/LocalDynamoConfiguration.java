package no.bibsys;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import no.bibsys.controllers.DatabaseController;
import no.bibsys.controllers.DatabaseControllerExcepctionHandler;
import no.bibsys.db.DatabaseManager;
import no.bibsys.db.TableDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DatabaseControllerExcepctionHandler.class)
public class LocalDynamoConfiguration {


    @Bean
    public DatabaseController getController(DatabaseManager databaseManager) {
        return new DatabaseController(databaseManager);
    }

    @Bean
    public DatabaseManager getDatabaseManager(TableDriver tableDriver) {
        return new DatabaseManager(tableDriver);
    }


    @Bean
    public TableDriver getTableDriver() {
        System.setProperty("java.library.path", "native-libs");
        AmazonDynamoDB client = DynamoDBEmbedded.create()
            .amazonDynamoDB();

        DynamoDB dynamoDb = new DynamoDB(client);
        return TableDriver.create(client, dynamoDb);
    }

}
