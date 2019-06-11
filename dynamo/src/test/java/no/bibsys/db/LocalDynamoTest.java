package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPI;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetResourcesRequest;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetResourcesResult;
import com.amazonaws.services.resourcegroupstaggingapi.model.ResourceTagMapping;

import no.bibsys.db.structures.RegistryStatus;
import org.junit.Before;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
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
        AWSResourceGroupsTaggingAPI mockTaggingClient = Mockito.mock(AWSResourceGroupsTaggingAPI.class); 
        AWSLambda mockLambdaClient = Mockito.mock(AWSLambda.class);
        GetResourcesResult mockResourcesResult = Mockito.mock(GetResourcesResult.class);
        
        @SuppressWarnings("unchecked")
        List<ResourceTagMapping> mockGetResourceTagMappingList = mock(List.class);
        ResourceTagMapping mockResourceTagMapping = mock(ResourceTagMapping.class);
        when(mockGetResourceTagMappingList.get(anyInt())).thenReturn(mockResourceTagMapping);
        when(mockResourceTagMapping.getResourceARN()).thenReturn("arn:fake");
        when(mockGetResourceTagMappingList.size()).thenReturn(1);
        when(mockTaggingClient.getResources(any(GetResourcesRequest.class))).thenReturn( mockResourcesResult);
        when(mockResourcesResult.getResourceTagMappingList()).thenReturn(mockGetResourceTagMappingList);

        registryManager = new RegistryManager(localClient, mockTaggingClient, mockLambdaClient);
        entityManager = new EntityManager(localClient, mockTaggingClient, mockLambdaClient);

        sampleData = new SampleData();

    }

    protected TableDriver newTableDriver() {
        AWSResourceGroupsTaggingAPI mockTaggingClient = Mockito.mock(AWSResourceGroupsTaggingAPI.class); 
        AWSLambda mockLambdaClient = Mockito.mock(AWSLambda.class); 
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
