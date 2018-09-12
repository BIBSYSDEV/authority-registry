package no.bibsys.db;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DatabaseManagerTest extends LocalDynamoTest {

  @Autowired
  DatabaseManager databaseManager;


  @Autowired
  TableDriver tableDriver;


  @Test
  public void DatabaseManagerShouldCreateATable() throws InterruptedException {
    String tableName = "DBManagerTest";
    boolean existsBeforeCreation = databaseManager.registryExists(tableName);

    databaseManager.createRegistry(tableName);
    boolean existsAfterCreation = databaseManager.registryExists(tableName);

    assertThat(existsBeforeCreation, is(equalTo(false)));
    assertThat(existsAfterCreation, is(equalTo(true)));

  }


  @Test
  public void DatabaseManagerShouldCheckIfARegistryExists() throws InterruptedException {
    DatabaseManagerShouldCreateATable();
  }


}
