package no.bibys;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import no.bibsys.db.DynamoDBConfiguration;
import no.bibsys.db.TableCreator;
import no.bibsys.db.TableDriver;
import no.bibsys.db.TableWriter;
import no.bibys.handlers.DatabaseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("no.bibsys.db")
public class HandlerConfiguration {

  @Bean
  public DynamoDBConfiguration dynamoDBConfiguration(){
    return new DynamoDBConfiguration();
  }


  @Bean
  public DatabaseHandler getDatabaseHandler(){
    return new DatabaseHandler();
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
