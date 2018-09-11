package no.bibsys;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import no.bibsys.db.TableCreator;
import no.bibsys.db.TableDriver;
import no.bibsys.db.TableWriter;
import no.bibsys.handlers.DatabaseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HandlerConfiguration {


  @Bean
  public DatabaseHandler getDatabaseHandler(TableCreator tableCreator,TableWriter writer){
    DatabaseHandler databaseHandler = new DatabaseHandler();
    databaseHandler.setTableCreator(tableCreator);
    databaseHandler.setTableWriter(writer);
    return databaseHandler;

  }

  @Bean
  public AmazonDynamoDB getClient(){
    return AmazonDynamoDBClientBuilder.standard().build();
  }

  @Bean
  public DynamoDB getDynamoDB(AmazonDynamoDB client){
    return new DynamoDB(client);
  }



  @Bean
  public TableDriver tableDriver(AmazonDynamoDB client, DynamoDB dynamoDB){
    return new TableDriver(client,dynamoDB);
  }

  @Bean
  public TableWriter getTableWriter(TableDriver tableDriver){
    return new TableWriter(tableDriver);
  }


  @Bean
  public TableCreator getTableCreator(TableDriver tableDriver){
    return new TableCreator(tableDriver);
  }

  @Bean
  public String getHelloString(){
    return "hello world";
  }


}