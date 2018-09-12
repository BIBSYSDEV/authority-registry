package no.bibsys.db;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

public class DatabaseManagerTest extends LocalDynamoTest {

  @Autowired
  DatabaseManager databaseManager;


  @Autowired
  TableDriver tableDriver;


  @Test
  @DirtiesContext
  public void DatabaseManagerShouldCreateATable()
      throws InterruptedException {
    String tableName = "DBManagerTest";
    boolean existsBeforeCreation = databaseManager.registryExists(tableName);

    databaseManager.createRegistry(tableName);
    boolean existsAfterCreation = databaseManager.registryExists(tableName);

    assertThat(existsBeforeCreation, is(equalTo(false)));
    assertThat(existsAfterCreation, is(equalTo(true)));

  }


  @Test(expected = TableAlreadyExistsException.class)
  @DirtiesContext
  public void DatabaseManagerShouldThrowAnExceptionWhenTryingToCreateAnExistingTable()
      throws InterruptedException {
    String tableName = "DBManagerTest";
    boolean existsBeforeCreation = databaseManager.registryExists(tableName);
    assertThat("The table should not exist before creation",
        existsBeforeCreation, is(equalTo(false)));
    databaseManager.createRegistry(tableName);
    boolean existsAfterCreation = databaseManager.registryExists(tableName);
    assertThat("The table should  exist before creation",
        existsAfterCreation, is(equalTo(true)));

    databaseManager.createRegistry(tableName);


  }


  @Test
  @DirtiesContext
  public void DatabaseManagerShouldCheckIfARegistryExists()
      throws InterruptedException, TableAlreadyExistsException {
    DatabaseManagerShouldCreateATable();
  }


}
