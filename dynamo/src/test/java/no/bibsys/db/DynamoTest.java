package no.bibsys.db;


import no.bibsys.db.structures.SimpleEntry;

public abstract class DynamoTest {

  String tableName="itemTable";
  SimpleEntry entry;

  public abstract void init();


}
