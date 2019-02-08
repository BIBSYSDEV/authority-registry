package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import no.bibsys.db.structures.RegistryStatus;
import org.junit.Before;
import org.mockito.Mockito;

import java.io.IOException;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

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

    protected RegistryManager registryManagerThatFailsToCreateATable() throws IOException {
        TableDriver mockDriver = Mockito.mock(TableDriver.class);
        DynamoDBMapper mapper = Mockito.mock(DynamoDBMapper.class);
        when(mockDriver.status(anyString())).thenReturn(RegistryStatus.ACTIVE.name())
                .thenReturn(RegistryStatus.NOT_FOUND.name());
        when(mockDriver.tableExists(anyString())).thenReturn(true);
        return new RegistryManager(mockDriver, mapper);

    }


    protected RegistryManager registryManagerThatIsCreatingMetadataTable() throws IOException {
        TableDriver mockDriver = Mockito.mock(TableDriver.class);
        DynamoDBMapper mapper = Mockito.mock(DynamoDBMapper.class);
        when(mockDriver.status(anyString())).thenReturn(RegistryStatus.UPDATING.name());
        when(mockDriver.tableExists(anyString())).thenReturn(true);
        return new RegistryManager(mockDriver, mapper);

    }

    protected RegistryManager registryManagerThatFailsCreatingMetadataTable() throws IOException {
        TableDriver mockDriver = Mockito.mock(TableDriver.class);
        DynamoDBMapper mapper = Mockito.mock(DynamoDBMapper.class);
        when(mockDriver.status(anyString())).thenReturn(RegistryStatus.NOT_FOUND.name());
        when(mockDriver.tableExists(anyString())).thenReturn(true);
        return new RegistryManager(mockDriver, mapper);

    }

}
