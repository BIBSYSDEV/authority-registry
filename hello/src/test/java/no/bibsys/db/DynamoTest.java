package no.bibsys.db;

import no.bibys.db.TableCreator;
import no.bibys.db.structures.SimpleEntry;

public abstract class DynamoTest {

  String tableName="itemTable";
  SimpleEntry entry;
  TableCreator tableCreator;

  public abstract void init();


}
