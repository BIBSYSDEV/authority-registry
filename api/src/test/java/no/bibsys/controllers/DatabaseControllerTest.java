package no.bibsys.controllers;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import no.bibsys.db.TableCreator;
import no.bibsys.db.TableDriver;
import no.bibsys.handlers.CreateRegistryRequest;
import no.bibsys.testtemplates.LocalDynamoTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

public class DatabaseControllerTest extends LocalDynamoTest {


    @Autowired
    DatabaseController databaseController;

    @Autowired
    TableDriver tableDriver;

    TableCreator tableCreator;

    @Before
    public void init() {
        tableCreator = new TableCreator(tableDriver);
    }

    @Test
    @DirtiesContext
    public void databaseControllerShouldCreateANewRegistry() throws InterruptedException {
        CreateRegistryRequest registryRequest =
            new CreateRegistryRequest("MyControllerTestTable");

        databaseController.createRegistry(registryRequest);
        boolean tableExists = tableCreator.tableExists(registryRequest.getRegistryName());
        assertThat(tableExists, is(equalTo(true)));


    }


    @Test(expected = TableAlreadyExistsException.class)
    @DirtiesContext
    public void databaseControllerShouldThrowExceptionWhenTryingToCreateAnExistinTable()
        throws InterruptedException {
        CreateRegistryRequest registryRequest =
            new CreateRegistryRequest("MyControllerTestTable");
        boolean tableExists = tableCreator.tableExists(registryRequest.getRegistryName());
        assertThat("Table should not existe before creation", tableExists, is(equalTo(false)));
        databaseController.createRegistry(registryRequest);
        tableExists = tableCreator.tableExists(registryRequest.getRegistryName());
        assertThat("Table should not existe before creation", tableExists, is(equalTo(true)));
        databaseController.createRegistry(registryRequest);


    }


}
