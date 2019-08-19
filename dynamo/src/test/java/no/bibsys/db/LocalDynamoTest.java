package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPI;
import no.bibsys.db.helpers.AwsLambdaMock;
import no.bibsys.db.helpers.AwsResourceGroupsTaggingApiMock;
import no.bibsys.db.helpers.AwsResourceGroupsTaggingApiMockBuilder;
import no.bibsys.db.structures.RegistryStatus;
import org.junit.AfterClass;
import org.junit.Before;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class LocalDynamoTest extends DynamoTest {

    private static final String SQLITE_4_JAVA_LIBRARY_PATH = "sqlite4java.library.path";
    private static final String JAVA_LIBRARY_PATH = "java.library.path";
    protected AmazonDynamoDB localClient;
    protected RegistryManager registryManager;
    protected EntityManager entityManager;
    protected SampleData sampleData;

    @Before
    public void init() throws IOException {
        System.setProperty(SQLITE_4_JAVA_LIBRARY_PATH, "build/libs");
        System.setProperty(JAVA_LIBRARY_PATH, "native-libs");

        localClient = DynamoDBEmbedded.create().amazonDynamoDB();

        AWSLambda mockLambdaClient = AwsLambdaMock.build();
        AWSResourceGroupsTaggingAPI mockTaggingClient = setUpTagging();

        registryManager = new RegistryManager(localClient, mockTaggingClient, mockLambdaClient);
        entityManager = new EntityManager(localClient, mockTaggingClient, mockLambdaClient);

        sampleData = new SampleData();
    }

    @AfterClass
    public static void deInit() {
        System.clearProperty(SQLITE_4_JAVA_LIBRARY_PATH);
    }

    private AWSResourceGroupsTaggingAPI setUpTagging() {

        AwsResourceGroupsTaggingApiMockBuilder awsResourceGroupsTaggingApiMockBuilder =
                new AwsResourceGroupsTaggingApiMockBuilder();
        AwsResourceGroupsTaggingApiMock awsResourceGroupsTaggingApiMock = awsResourceGroupsTaggingApiMockBuilder
                .withMatchableResourceTagMapping("someStack").build();

        return awsResourceGroupsTaggingApiMock.initialize();
    }

    protected TableDriver newTableDriver() {
        AWSResourceGroupsTaggingAPI mockTaggingClient = setUpTagging();
        AWSLambda mockLambdaClient = AwsLambdaMock.build();
        return new TableDriver(localClient, mockTaggingClient, mockLambdaClient);
    }

    protected RegistryManager registryManagerThatFailsToCreateATable() throws IOException {
        TableDriver mockDriver = mock(TableDriver.class);
        DynamoDBMapper mapper = mock(DynamoDBMapper.class);
        when(mockDriver.status(anyString())).thenReturn(RegistryStatus.ACTIVE.name())
                .thenReturn(RegistryStatus.NOT_FOUND.name());
        when(mockDriver.tableExists(anyString())).thenReturn(true);
        return new RegistryManager(mockDriver, mapper);

    }

    protected RegistryManager registryManagerThatIsCreatingMetadataTable() throws IOException {
        TableDriver mockDriver = mock(TableDriver.class);
        when(mockDriver.findDynamoTriggerArn()).thenReturn("SomeArn");
        DynamoDBMapper mapper = mock(DynamoDBMapper.class);
        when(mockDriver.status(anyString())).thenReturn(RegistryStatus.UPDATING.name());
        when(mockDriver.tableExists(anyString())).thenReturn(true);
        return new RegistryManager(mockDriver, mapper);

    }

    protected RegistryManager registryManagerThatFailsCreatingMetadataTable() throws IOException {
        TableDriver mockDriver = mock(TableDriver.class);
        when(mockDriver.findDynamoTriggerArn()).thenReturn("SomeArn");
        DynamoDBMapper mapper = mock(DynamoDBMapper.class);
        when(mockDriver.status(anyString())).thenReturn(RegistryStatus.NOT_FOUND.name());
        when(mockDriver.tableExists(anyString())).thenReturn(true);
        return new RegistryManager(mockDriver, mapper);

    }

}
