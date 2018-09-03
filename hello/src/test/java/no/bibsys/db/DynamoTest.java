package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import no.bibys.db.TableCreator;
import no.bibys.db.structures.SimpleEntry;

public abstract class DynamoTest {

  String tableName="itemTable";
  SimpleEntry entry;

  AmazonDynamoDB client;
  TableCreator tableCreator;



  public abstract void init();


}
