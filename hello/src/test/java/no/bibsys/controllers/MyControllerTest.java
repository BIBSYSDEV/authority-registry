package no.bibsys.controllers;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import no.bibsys.db.LocalDynamoTest;
import no.bibsys.db.TableCreator;
import no.bibsys.handlers.CreateRegistryRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

public class MyControllerTest extends LocalDynamoTest {


  @Autowired
  MyController myController;

  @Autowired
  TableCreator tableCreator;

  @Test
  @DirtiesContext
  public void MyControllerShouldCreateANewRegistry() throws InterruptedException {
    CreateRegistryRequest registryRequest =
        new CreateRegistryRequest("MyControllerTestTable");

    myController.createRegistry(registryRequest);
    boolean tableExists = tableCreator.tableExists(registryRequest.getRegistryName());
    assertThat(tableExists, is(equalTo(true)));


  }


  @Test(expected = TableAlreadyExistsException.class)
  @DirtiesContext
  public void MyControllerShouldThrowExceptionWhenTryingToCreateAnExistinTable()
      throws InterruptedException {
    CreateRegistryRequest registryRequest =
        new CreateRegistryRequest("MyControllerTestTable");
    boolean tableExists = tableCreator.tableExists(registryRequest.getRegistryName());
    assertThat("Table should not existe before creation", tableExists, is(equalTo(false)));
    myController.createRegistry(registryRequest);
    tableExists = tableCreator.tableExists(registryRequest.getRegistryName());
    assertThat("Table should not existe before creation", tableExists, is(equalTo(true)));
    myController.createRegistry(registryRequest);


  }


}
