package no.bibys;

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
  public TableWriter getTableWriter(TableDriver tableDriver){
    return new TableWriter(tableDriver);
  }


  @Bean
  public TableCreator getTableCreator(TableDriver tableDriver){
    return new TableCreator(tableDriver);
  }


}
