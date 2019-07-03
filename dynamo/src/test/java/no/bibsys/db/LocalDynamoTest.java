package no.bibsys.db;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.mockito.Mockito;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPI;

import no.bibsys.db.helpers.AwsLambdaMock;
import no.bibsys.db.helpers.AwsResourceGroupsTaggingApiMock;
import no.bibsys.db.structures.RegistryStatus;

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
        
        AWSLambda mockLambdaClient = AwsLambdaMock.build();
        AWSResourceGroupsTaggingAPI mockTaggingClient = AwsResourceGroupsTaggingApiMock.build(); 

        registryManager = new RegistryManager(localClient, mockTaggingClient, mockLambdaClient);
        entityManager = new EntityManager(localClient, mockTaggingClient, mockLambdaClient);

        sampleData = new SampleData();

    }

    protected TableDriver newTableDriver() {
        AWSResourceGroupsTaggingAPI mockTaggingClient = AwsResourceGroupsTaggingApiMock.build(); 
        AWSLambda mockLambdaClient = AwsLambdaMock.build(); 
        return new TableDriver(localClient, mockTaggingClient, mockLambdaClient);
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
