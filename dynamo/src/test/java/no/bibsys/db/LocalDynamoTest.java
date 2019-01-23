package no.bibsys.db;

import java.io.IOException;
import org.junit.Before;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class LocalDynamoTest extends DynamoTest {

    protected AmazonDynamoDB localClient;
    protected RegistryManager registryManager;
    protected EntityManager entityManager;
    protected SampleData sampleData;

    @Before
    public void init() throws IOException {
        System.setProperty("sqlite4java.library.path", "build/libs");
        System.setProperty("java.library.path", "native-libs");
        System.setProperty("sqlite4java.library.path", "build/libs");

        localClient = DynamoDBEmbedded.create().amazonDynamoDB();
        registryManager = new RegistryManager(localClient);
        entityManager = new EntityManager(localClient);

        sampleData = new SampleData();

    }

    protected TableDriver newTableDriver() {
        return TableDriver.create(localClient);
    }
    
    

}
