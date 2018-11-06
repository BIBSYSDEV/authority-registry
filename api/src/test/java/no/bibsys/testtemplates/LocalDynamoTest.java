package no.bibsys.testtemplates;

import org.junit.Before;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import no.bibsys.LocalDynamoDBHelper;
import no.bibsys.db.DatabaseManager;
import no.bibsys.db.RegistryManager;
import no.bibsys.db.TableDriver;

public abstract class LocalDynamoTest {

    public DatabaseManager databaseManager;
    public RegistryManager registryManager;
    public SampleData sampleData;

    @Before
    public void setUp() {
        System.setProperty("sqlite4java.library.path", "build/libs");

        final AmazonDynamoDB client = LocalDynamoDBHelper.getClient();

        TableDriver tableDriver = TableDriver.create(client, new DynamoDB(client));
        databaseManager = new DatabaseManager(tableDriver);
        registryManager = new RegistryManager(tableDriver);

        sampleData = new SampleData();
    }
}
