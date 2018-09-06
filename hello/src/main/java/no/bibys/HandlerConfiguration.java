package no.bibys;

import no.bibsys.db.DynamoDBConfiguration;
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


}
