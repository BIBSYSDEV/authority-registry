package no.bibsys.db;


import no.bibsys.db.structures.SimpleEntry;

public abstract class DynamoTest {

  String tableName="itemTable";
  SimpleEntry entry;
  TableCreator tableCreator;

  public abstract void init();


}
