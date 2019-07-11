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
import com.amazonaws.services.lambda.model.CreateEventSourceMappingResult;
import com.amazonaws.services.lambda.model.EventSourcePosition;
import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPI;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetResourcesRequest;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetResourcesResult;
import com.amazonaws.services.resourcegroupstaggingapi.model.TagFilter;
import no.bibsys.db.structures.Entity;
import no.bibsys.db.structures.Registry;
import no.bibsys.db.structures.RegistryStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

public class TableDriver {

    private static final String TABLECLASS_TAG_KEY = "no.unit.entitydata.tableclass";
    private static final Logger logger = LoggerFactory.getLogger(TableDriver.class);
    public static final String AWS_CLOUDFORMATION_STACK_NAME = "aws: cloudformation: stack - name";
    public static final String DYNAMO_DB_TRIGGER_EVENT_PROCESSOR = "DynamoDBTrigger_EventProcessor";
    public static final String DYNAMO_DB_EVENT_PROCESSOR_LAMBDA = "DynamoDBEventProcessorLambda";
    public static final String UNIT_RESOURCE_TYPE = "unit.resource_type";
    public static final String AWS_CLOUDFORMATION_LOGICAL_ID = "aws:cloudformation:logical-id";
    public static final int SINGLE_ITEM = 1;
    private final transient AmazonDynamoDB client;
    private final transient DynamoDB dynamoDb;
    private final transient DynamoDBMapper mapper;
    private final transient AWSResourceGroupsTaggingAPI taggingAPIclient;
    private final transient AWSLambda lambdaClient;


    public TableDriver(final AmazonDynamoDB client, 
            AWSResourceGroupsTaggingAPI taggingAPIclient, 
            AWSLambda lambdaClient) {
        
        if (isNull(client)) {
            throw new IllegalStateException("Cannot set null client ");
        }
        if (isNull(taggingAPIclient)) {
            throw new IllegalStateException("Cannot set null taggingAPIclient ");
        }
        
        if (isNull(lambdaClient)) {
            throw new IllegalStateException("Cannot set null lambdaClient ");
        }

        this.client = client;
        this.dynamoDb = new DynamoDB(client);
        this.mapper = new DynamoDBMapper(client);
        this.taggingAPIclient = taggingAPIclient;
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
        return createEntityTableWithStreamsAndTags(tableName) &&  connectTableToTrigger(tableName);

    }

    public void createRegistryMetadataTable(final String tableName) {
        createTable(tableName, Registry.class);
    }

    private boolean createTable(final String tableName, Class<?> clazz) {

        if (!tableExists(tableName)) {
            DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                    .withTableNameOverride(TableNameOverride.withTableNameReplacement(tableName)).build();

            CreateTableRequest request = mapper.generateCreateTableRequest(clazz, config);
            request.setProvisionedThroughput(
                    new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));

            
            Collection<Tag> tags = Collections.singleton(
                    new Tag().withKey(TABLECLASS_TAG_KEY).withValue(clazz.getSimpleName())
                    );
            request.setTags(tags);

            
            TableUtils.createTableIfNotExists(client, request);
            logger.debug("Table create request sendt, tableId={} with tags={}", tableName, tags);
            return true;
        }
        logger.warn("Tried to create table but it already exists, tableName={}", tableName);
        return false;
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
            logger.debug("Table create request sendt, tableId={} with tags={}, returning TRUE", tableName, tags);
            return true;
        }
        logger.error("Tried to create table but it already exists, tableName={}", tableName);
        return false;
    }

    
    

    private boolean connectTableToTrigger(final String tableName) {
        try {
            logger.debug("connectTableToTrigger, Waiting for table:{}  to be created", tableName);
            TableUtils.waitUntilExists(client, tableName);
            logger.debug("Table:{} created, getting info", tableName);
            DescribeTableResult describeTable = client.describeTable(tableName);
            String eventSourceArn = describeTable.getTable().getLatestStreamArn();

            logger.debug("Table({}) has ARN={}", tableName, eventSourceArn);
            
            TagFilter tagFiltersAWS = new TagFilter()
                    .withKey(AWS_CLOUDFORMATION_LOGICAL_ID).withValues(DYNAMO_DB_EVENT_PROCESSOR_LAMBDA);
            TagFilter tagFiltersUNIT = new TagFilter()
                  .withKey(UNIT_RESOURCE_TYPE).withValues(DYNAMO_DB_TRIGGER_EVENT_PROCESSOR);
            TagFilter tagFilterStackName = new TagFilter().withKey(AWS_CLOUDFORMATION_STACK_NAME)
                    .withValues(findStackName());

            logger.debug("Created tag filters {}: {}, {}: {}, {}: {}", AWS_CLOUDFORMATION_LOGICAL_ID,
                    DYNAMO_DB_EVENT_PROCESSOR_LAMBDA, UNIT_RESOURCE_TYPE, DYNAMO_DB_TRIGGER_EVENT_PROCESSOR,
                    AWS_CLOUDFORMATION_STACK_NAME, findStackName());
            logger.debug("The available resources are: ", new GetResourcesRequest());

            GetResourcesRequest getResourcesRequest = 
                    new GetResourcesRequest()
                    .withTagFilters(tagFiltersAWS)
                    .withTagFilters(tagFiltersUNIT)
                    .withTagFilters(tagFilterStackName);

            logger.debug("getResourcesRequest={}",getResourcesRequest);
            GetResourcesResult resources =  taggingAPIclient.getResources(getResourcesRequest); 

            String res = Optional.ofNullable(resources.toString()).orElse("did no exist");

            logger.debug("Resources is {} and resource tag mapping size is {}",
                    res, resources.getResourceTagMappingList().size());

            if (resources.getResourceTagMappingList().size() == SINGLE_ITEM) {
                logger.debug("matching resources={}",resources);
            
                String functionNameARN  = resources.getResourceTagMappingList().get(0).getResourceARN();
                logger.debug("CloudSearch trigger ARN: {}", functionNameARN);
                CreateEventSourceMappingRequest createEventSourceMappingRequest = 
                new CreateEventSourceMappingRequest()
                        .withEventSourceArn(eventSourceArn)
                        .withStartingPosition(EventSourcePosition.LATEST)
                        .withFunctionName(functionNameARN);
                logger.debug("Event source mapping request: {}", createEventSourceMappingRequest);
                CreateEventSourceMappingResult createEventSourceMappingResult = 
                        lambdaClient.createEventSourceMapping(createEventSourceMappingRequest);
                logger.debug("eventSourceMapping created, createEventSourceMappingResult={}",
                        createEventSourceMappingResult);
                return true;
            }
        } catch (Exception e) {
            logger.error("Exception in connectTableToTrigger, tableName={}!",tableName, e);
            return false;
        }
        logger.debug("NO matching resources, returning FALSE without creating trigger");
        return false;
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
        return System.getenv("STACK_NAME");
    }
}
