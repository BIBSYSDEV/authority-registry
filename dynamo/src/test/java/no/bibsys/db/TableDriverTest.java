package no.bibsys.db;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPI;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetResourcesRequest;
import com.amazonaws.services.resourcegroupstaggingapi.model.TagFilter;
import no.bibsys.db.exceptions.ResourceFilteringException;
import no.bibsys.db.helpers.AwsLambdaMock;
import no.bibsys.db.helpers.AwsResourceGroupsTaggingApiMock;
import no.bibsys.db.helpers.AwsResourceGroupsTaggingApiMockBuilder;
import no.bibsys.db.structures.Entity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TableDriverTest extends LocalDynamoTest {

    private static final String AWS_CLOUDFORMATION_LOGICAL_ID = "aws:cloudformation:logical-id";
    private static final String DYNAMO_DB_EVENT_PROCESSOR_LAMBDA = "DynamoDBEventProcessorLambda";
    private static final String UNIT_RESOURCE_TYPE = "unit.resource_type";
    private static final String DYNAMO_DB_TRIGGER_EVENT_PROCESSOR = "DynamoDBTrigger_EventProcessor";
    private static final String AWS_CLOUDFORMATION_STACK_NAME = "aws:cloudformation:stack-name";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test(expected = IllegalStateException.class)
    public void constructor_nullValue_exception() {
        new TableDriver(null, null, null);
    }

    @Test
    public void createTable_TableNotExisting_AddsTable() {
        TableDriver tableDriver = newTableDriver();
        tableDriver.createEntityRegistryTable(tableName);
        List<String> tables = tableDriver.listTables();
        int numberOfTables = tables.size();

        assertThat(numberOfTables, is(equalTo(1)));
    }

    @Test
    public void createTable_TableExists_ReturnsFalse() {
        TableDriver tableDriver = newTableDriver();
        int tables = tableDriver.listTables().size();
        assertThat(tables, is(equalTo(0)));

        tableDriver.createEntityRegistryTable(tableName);
        boolean createDuplicateTable = tableDriver.createEntityRegistryTable(tableName);
        assertThat(createDuplicateTable, is(equalTo(false)));

        tables = tableDriver.listTables().size();
        assertThat(tables, is(equalTo(1)));
    }

    @Test
    public void deleteTable_EmptyTable_ReturnsTrue() {
        TableDriver tableDriver = newTableDriver();
        tableDriver.createEntityRegistryTable(tableName);

        boolean deleteTable = tableDriver.deleteTable(tableName);
        assertThat(deleteTable, equalTo(true));

        int tables = tableDriver.listTables().size();
        assertThat(tables, is(equalTo(0)));
    }

    @Test
    public void deleteTable_TableNotExisting_ReturnsFalse() {
        TableDriver tableDriver = newTableDriver();
        tableDriver.createEntityRegistryTable(tableName);

        boolean deleteTable = tableDriver.deleteTable(tableName + "blabla");
        assertThat(deleteTable, equalTo(false));

        int tables = tableDriver.listTables().size();

        assertThat(tables, is(equalTo(1)));
    }

    @Test
    public void deleteTable_TableNotEmpty_ReturnsTrue() {
        TableDriver tableDriver = newTableDriver();
        tableDriver.createEntityRegistryTable(tableName);

        AwsResourceGroupsTaggingApiMockBuilder awsResourceGroupsTaggingApiMockBuilder
                = new AwsResourceGroupsTaggingApiMockBuilder();
        awsResourceGroupsTaggingApiMockBuilder.withMatchableResourceTagMapping("someStackName");
        AwsResourceGroupsTaggingApiMock awsResourceGroupsTaggingApiMock =
                awsResourceGroupsTaggingApiMockBuilder.build();
        AWSResourceGroupsTaggingAPI mockTaggingClient = awsResourceGroupsTaggingApiMock.initialize();
        AWSLambda mockLambdaClient = AwsLambdaMock.build();

        EntityManager entityManager = new EntityManager(localClient, mockTaggingClient, mockLambdaClient);
        entityManager.addEntity(tableName, new Entity());

        boolean deleteTable = tableDriver.deleteTable(tableName);
        assertThat(deleteTable, equalTo(true));
    }

    @Test
    public void listTable_FiveExistingTables_ListsAllFiveTables() {
        TableDriver tableDriver = newTableDriver();
        tableDriver.createEntityRegistryTable("test");
        tableDriver.createEntityRegistryTable("test1");
        tableDriver.createEntityRegistryTable("test2");
        tableDriver.createEntityRegistryTable("test3");
        tableDriver.createEntityRegistryTable("test4");

        List<String> tables = tableDriver.listTables();
        assertTrue(tables.contains("test"));
        assertTrue(tables.contains("test1"));
        assertTrue(tables.contains("test2"));
        assertTrue(tables.contains("test3"));
        assertTrue(tables.contains("test4"));
    }

    @Test
    public void tooManyResourceTagMappingsRaisesException() {
        expectedException.expect(ResourceFilteringException.class);
        expectedException.expectMessage("The resource filter failed, list length should be 1, but was 2");

        AWSLambda mockLambdaClient = AwsLambdaMock.build();
        AwsResourceGroupsTaggingApiMockBuilder awsResourceGroupsTaggingApiMockBuilder =
                new AwsResourceGroupsTaggingApiMockBuilder();
        String stackName = "aStackNameRepeatedProbablyShouldNeverHappen";
        awsResourceGroupsTaggingApiMockBuilder.withMatchableResourceTagMapping(stackName)
                .withMatchableResourceTagMapping(stackName);
        AwsResourceGroupsTaggingApiMock awsResourceGroupsTaggingApiMock =
                awsResourceGroupsTaggingApiMockBuilder.build();
        AWSResourceGroupsTaggingAPI taggingAPI = awsResourceGroupsTaggingApiMock.initialize();
        TableDriver tableDriver = new TableDriver(localClient, taggingAPI, mockLambdaClient, stackName);
        tableDriver.findDynamoTriggerArn();
    }

    @Test
    public void matchesOneAndOnlyOneMatchingResourceMapping() {
        AWSLambda mockLambdaClient = AwsLambdaMock.build();
        AwsResourceGroupsTaggingApiMockBuilder awsResourceGroupsTaggingApiMockBuilder =
                new AwsResourceGroupsTaggingApiMockBuilder();
        String stackName = "aSingleStackName";

        TagFilter tagFiltersAws = new TagFilter()
                .withKey(AWS_CLOUDFORMATION_LOGICAL_ID).withValues(DYNAMO_DB_EVENT_PROCESSOR_LAMBDA);
        TagFilter tagFiltersUnit = new TagFilter()
                .withKey(UNIT_RESOURCE_TYPE).withValues(DYNAMO_DB_TRIGGER_EVENT_PROCESSOR);
        TagFilter tagFilterStackName = new TagFilter().withKey(AWS_CLOUDFORMATION_STACK_NAME)
                .withValues(stackName);
        GetResourcesRequest getResourcesRequest =
                new GetResourcesRequest()
                        .withTagFilters(tagFiltersAws)
                        .withTagFilters(tagFiltersUnit)
                        .withTagFilters(tagFilterStackName);

        awsResourceGroupsTaggingApiMockBuilder.withMatchableResourceTagMapping(stackName);
        AwsResourceGroupsTaggingApiMock awsResourceGroupsTaggingApiMock =
                awsResourceGroupsTaggingApiMockBuilder.build();
        awsResourceGroupsTaggingApiMock.setGetResourcesRequest(getResourcesRequest);
        AWSResourceGroupsTaggingAPI taggingApi = awsResourceGroupsTaggingApiMock.initialize();

        TableDriver tableDriver = new TableDriver(localClient, taggingApi, mockLambdaClient, stackName);
        assertThat(tableDriver.findDynamoTriggerArn(), is(not(isEmptyOrNullString())));
    }
}
