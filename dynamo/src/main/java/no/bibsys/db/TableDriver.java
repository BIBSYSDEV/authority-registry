package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.Select;
import com.amazonaws.services.dynamodbv2.model.StreamSpecification;
import com.amazonaws.services.dynamodbv2.model.StreamViewType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.Tag;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.CreateEventSourceMappingRequest;
import com.amazonaws.services.lambda.model.EventSourcePosition;
import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPI;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetResourcesRequest;
import com.amazonaws.services.resourcegroupstaggingapi.model.ResourceTagMapping;
import com.amazonaws.services.resourcegroupstaggingapi.model.TagFilter;
import no.bibsys.db.exceptions.ResourceFilteringException;
import no.bibsys.db.structures.Entity;
import no.bibsys.db.structures.Registry;
import no.bibsys.db.structures.RegistryStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class TableDriver {

    private static final String TABLECLASS_TAG_KEY = "no.unit.entitydata.tableclass";
    private static final Logger logger = LoggerFactory.getLogger(TableDriver.class);
    public static final String AWS_CLOUDFORMATION_STACK_NAME = "aws: cloudformation: stack - name";
    public static final String DYNAMO_DB_TRIGGER_EVENT_PROCESSOR = "DynamoDBTrigger_EventProcessor";
    public static final String DYNAMO_DB_EVENT_PROCESSOR_LAMBDA = "DynamoDBEventProcessorLambda";
    public static final String UNIT_RESOURCE_TYPE = "unit.resource_type";
    public static final String AWS_CLOUDFORMATION_LOGICAL_ID = "aws:cloudformation:logical-id";
    public static final int SINGLE_ITEM = 1;
    private static final String UNIT_RESOURCE_TYPE = "unit.resource_type";
    private static final String AWS_CLOUDFORMATION_LOGICAL_ID = "aws:cloudformation:logical-id";
    private static final String AWS_CLOUDFORMATION_STACK_NAME = "aws:cloudformation:stack-name";
    private static final int SINGLE_ITEM = 1;
    private static final int ONLY_ELEMENT = 0;
    private static final String STACK_NAME = "STACK_NAME";
    private static final String DYNAMO_DB_EVENT_PROCESSOR_LAMBDA = "DynamoDBEventProcessorLambda";
    private static final String DYNAMO_DB_TRIGGER_EVENT_PROCESSOR = "DynamoDBTrigger_EventProcessor";
    private final transient AmazonDynamoDB client;
    private final transient DynamoDB dynamoDb;
    private final transient DynamoDBMapper mapper;
    private final transient AWSLambda lambdaClient;
    private final transient AWSResourceGroupsTaggingAPI taggingApiClient;
    private final transient AWSLambda lambdaClient;
    private final transient String stackName;

    public TableDriver(final AmazonDynamoDB client,
                       AWSResourceGroupsTaggingAPI taggingApiClient,
                       AWSLambda lambdaClient) {
        this(client, taggingApiClient, lambdaClient, null);

    }

    public TableDriver(final AmazonDynamoDB client,
                       AWSResourceGroupsTaggingAPI taggingApiClient,
                       AWSLambda lambdaClient, String stackName) {

        if (isNull(client)) {
            throw new IllegalStateException("Cannot set null client ");
        }
        if (isNull(taggingApiClient)) {
            throw new IllegalStateException("Cannot set null taggingApiClient ");
        }

        if (isNull(lambdaClient)) {
            throw new IllegalStateException("Cannot set null lambdaClient ");
        }

        this.stackName = nonNull(stackName) ? stackName : findStackName();

        this.client = client;
        this.dynamoDb = new DynamoDB(client);
        this.mapper = new DynamoDBMapper(client);
        this.taggingApiClient = taggingApiClient;
        this.lambdaClient = lambdaClient;
    }

    private Table getTable(final String tableName) {
        logger.debug("getTable tableName={}", tableName);
        return dynamoDb.getTable(tableName);
    }

    /**
     * Check if table exists.
     *
     * @param tableName The name of the table.
     * @return true if table exists, false otherwise
     */
    public boolean tableExists(final String tableName) {
        boolean exists = false;
        try {
            TableDescription describe = getTable(tableName).describe();
            String tableStatus = describe.getTableStatus();
            exists = tableStatus != null;
        } catch (ResourceNotFoundException e) {
            logger.debug("Table {} does not exist", tableName);
        }
        return exists;
    }

    public boolean isTableEmpty(final String tableName) {
        ScanRequest scanRequest = new ScanRequest(tableName).withSelect(Select.COUNT);
        ScanResult result = client.scan(scanRequest);
        Integer items = result.getScannedCount();
        return items == 0;
    }

    public boolean deleteTable(final String tableName) {
        if (!tableExists(tableName)) {
            logger.error("Can not delete non-existing table, tableId={}", tableName);
            return false;
        }
        DeleteTableRequest deleteRequest = new DeleteTableRequest(tableName);
        TableUtils.deleteTableIfExists(client, deleteRequest);
        return true;
    }

    public boolean createEntityRegistryTable(final String tableName) {
        return createEntityTableWithStreamsAndTags(tableName) && connectTableToTrigger(tableName);
    }

    public void createRegistryMetadataTable(final String tableName) {
        createTable(tableName, Registry.class);
    }

    private void createTable(final String tableName, Class<?> clazz) {
        if (!tableExists(tableName)) {
            DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                    .withTableNameOverride(TableNameOverride.withTableNameReplacement(tableName)).build();
            CreateTableRequest request = mapper.generateCreateTableRequest(clazz, config);
            request.setProvisionedThroughput(
                    new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));
            Collection<Tag> tags = Collections.singleton(
                    new Tag().withKey(TABLECLASS_TAG_KEY).withValue(clazz.getSimpleName()));
            request.setTags(tags);
            TableUtils.createTableIfNotExists(client, request);
            logger.debug("Table create request sendt, tableId={} with tags={}", tableName, tags);
            return;
        }
        logger.warn("Tried to create table but it already exists, tableName={}", tableName);
    }

    private boolean createEntityTableWithStreamsAndTags(final String tableName) {
        if (!tableExists(tableName)) {
            Class<Entity> clazz = Entity.class;
            DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                    .withTableNameOverride(TableNameOverride.withTableNameReplacement(tableName)).build();
            CreateTableRequest request = mapper.generateCreateTableRequest(clazz, config);
            request.setProvisionedThroughput(
                    new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));

            request.setStreamSpecification(
                    new StreamSpecification().withStreamEnabled(true)
                            .withStreamViewType(StreamViewType.NEW_AND_OLD_IMAGES));
            Collection<Tag> tags = Collections.singleton(
                    new Tag().withKey(TABLECLASS_TAG_KEY).withValue(clazz.getSimpleName())
            );
            request.setTags(tags);
            TableUtils.createTableIfNotExists(client, request);
            logger.debug("Table create request send, tableId={} with tags={}, returning TRUE", tableName, tags);
            return true;
        }
        logger.error("Tried to create table but it already exists, tableName={}", tableName);
        return false;
    }

    private boolean connectTableToTrigger(final String tableName) {
        try {
            logger.debug("connectTableToTrigger, Waiting for table:{}  to be created", tableName);
            TableUtils.waitUntilExists(client, tableName);
            DescribeTableResult describeTable = client.describeTable(tableName);
            String eventSourceArn = describeTable.getTable().getLatestStreamArn();
            CreateEventSourceMappingRequest createEventSourceMappingRequest =
                    new CreateEventSourceMappingRequest()
                            .withEventSourceArn(eventSourceArn)
                            .withStartingPosition(EventSourcePosition.LATEST)
                            .withFunctionName(findDynamoTriggerArn());
            lambdaClient.createEventSourceMapping(createEventSourceMappingRequest);
            return true;
        } catch (Exception e) {
            logger.error("Exception in connectTableToTrigger, tableName={}!", tableName, e);
            return false;
        }
    }

    protected String findDynamoTriggerArn() throws ResourceFilteringException {
        TagFilter tagFiltersAWS = new TagFilter()
                .withKey(AWS_CLOUDFORMATION_LOGICAL_ID).withValues(DYNAMO_DB_EVENT_PROCESSOR_LAMBDA);
        TagFilter tagFiltersUNIT = new TagFilter()
                .withKey(UNIT_RESOURCE_TYPE).withValues(DYNAMO_DB_TRIGGER_EVENT_PROCESSOR);
        TagFilter tagFilterStackName = new TagFilter().withKey(AWS_CLOUDFORMATION_STACK_NAME)
                .withValues(this.stackName);
        GetResourcesRequest getResourcesRequest =
                new GetResourcesRequest()
                        .withTagFilters(tagFiltersAWS)
                        .withTagFilters(tagFiltersUNIT)
                        .withTagFilters(tagFilterStackName);
        List<String> arnList = taggingApiClient.getResources(getResourcesRequest)
                .getResourceTagMappingList()
                .stream()
                .map(ResourceTagMapping::getResourceARN)
                .collect(Collectors.toList());

        int arnListSize = arnList.size();

        if (arnListSize != SINGLE_ITEM) {
            throw new ResourceFilteringException(arnListSize);
        }

        return arnList.get(ONLY_ELEMENT);

    }

    public List<String> listTables() {
        List<String> tableList = new ArrayList<>();
        dynamoDb.listTables().forEach(table -> tableList.add(table.getTableName()));
        logger.info("Listing {} tables", tableList.size());
        return tableList;
    }

    public String status(String tableName) {
        try {
            TableDescription describe = getTable(tableName).describe();
            return describe.getTableStatus();
        } catch (ResourceNotFoundException e) {
            return RegistryStatus.NOT_FOUND.name();
        }
    }

    private String findStackName() {
        return System.getenv(STACK_NAME);
    }
}
