package no.bibsys.testtemplates;

import org.junit.Before;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

import no.bibsys.LocalDynamoDBHelper;
import no.bibsys.db.EntityManager;
import no.bibsys.db.ItemDriver;
import no.bibsys.db.ItemManager;
import no.bibsys.db.RegistryManager;
import no.bibsys.db.TableDriver;
import no.bibsys.db.TableManager;

public abstract class LocalDynamoTest {

    protected RegistryManager registryManager;
    protected EntityManager entityManager;
    public SampleData sampleData;

    @Before
    public void setUp() {
        System.setProperty("sqlite4java.library.path", "build/libs");

        final AmazonDynamoDB client = LocalDynamoDBHelper.getClient();

        TableManager tableManager = new TableManager(TableDriver.create(client, new DynamoDB(client)));
        ItemManager itemManager = new ItemManager(ItemDriver.create(new DynamoDB(client)));
        registryManager = new RegistryManager(tableManager, itemManager);
        entityManager = new EntityManager(tableManager, itemManager);

        sampleData = new SampleData();
    }
}
