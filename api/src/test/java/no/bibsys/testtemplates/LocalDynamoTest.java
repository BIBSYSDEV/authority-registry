package no.bibsys.testtemplates;

import org.junit.Before;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import no.bibsys.LocalDynamoDBHelper;
import no.bibsys.MockEnvironment;
import no.bibsys.db.EntityManager;
import no.bibsys.db.ItemDriver;
import no.bibsys.db.RegistryManager;
import no.bibsys.db.TableDriver;
import no.bibsys.service.AuthenticationService;

public abstract class LocalDynamoTest {

    protected RegistryManager registryManager;
    protected EntityManager entityManager;
    protected AuthenticationService authenticationService;
    public SampleData sampleData;

    @Before
    public void setUp() {
        System.setProperty("sqlite4java.library.path", "build/libs");

        final AmazonDynamoDB client = LocalDynamoDBHelper.getClient();

        TableDriver tableManager = TableDriver.create(client);
        ItemDriver itemManager = ItemDriver.create(tableManager);
        authenticationService = new AuthenticationService(client, new MockEnvironment());
        registryManager = new RegistryManager(tableManager, itemManager, authenticationService,
                new MockEnvironment());
        entityManager = new EntityManager(itemManager);

        sampleData = new SampleData();
    }
}
