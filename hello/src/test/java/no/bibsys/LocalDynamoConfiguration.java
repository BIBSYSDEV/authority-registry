package no.bibsys;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import no.bibsys.controllers.MyController;
import no.bibsys.db.DatabaseManager;
import no.bibsys.db.TableCreator;
import no.bibsys.db.TableDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocalDynamoConfiguration {


  @Bean
  public MyController getController(DatabaseManager databaseManager) {
    return new MyController(databaseManager);
  }

  @Bean
  public DatabaseManager getDatabaseManager(TableCreator tableCreator) {
    return new DatabaseManager(tableCreator);
  }


  @Bean
  public TableCreator getTableCreator(TableDriver tableDriver) {
    return new TableCreator(tableDriver);
  }

  @Bean
  public TableDriver getTableDriver() {
    System.setProperty("java.library.path", "native-libs");
    AmazonDynamoDB client = DynamoDBEmbedded.create()
        .amazonDynamoDB();

    DynamoDB dynamoDB = new DynamoDB(client);
    return new TableDriver(client, dynamoDB);
  }

}
