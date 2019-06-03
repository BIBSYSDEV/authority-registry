package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
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
import no.bibsys.db.structures.Entity;
import no.bibsys.db.structures.Registry;
import no.bibsys.db.structures.RegistryStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.glue.model.CreateTriggerRequest;
import software.amazon.awssdk.services.glue.model.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

public class TableDriver {

    private static final String TABLECLASS_TAG_KEY = "no.unit.entitydata.tableclass";
    private static final Logger logger = LoggerFactory.getLogger(TableDriver.class);
    private final transient AmazonDynamoDB client;
    private final transient DynamoDB dynamoDb;
    private final transient DynamoDBMapper mapper;

    public TableDriver(final AmazonDynamoDB client) {
        if (isNull(client)) {
            throw new IllegalStateException("Cannot set null client ");
        }
        this.client = client;
        this.dynamoDb = new DynamoDB(client);
        this.mapper = new DynamoDBMapper(client);
    }

    private Table getTable(final String tableName) {
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
        return createTable(tableName, Entity.class);
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

            request.setStreamSpecification(
                    new StreamSpecification().withStreamEnabled(true)
                        .withStreamViewType(StreamViewType.NEW_AND_OLD_IMAGES));
            
            Collection<Tag> tags = Collections.singleton(
                    new Tag().withKey(TABLECLASS_TAG_KEY).withValue(clazz.getSimpleName())
                    );
            request.setTags(tags);
            
            
            TableUtils.createTableIfNotExists(client, request);
            logger.debug("Table created, tableId={} with tags={}", tableName, tags);
            
//            Action action = Action.builder(). build();
//            CreateTriggerRequest createTriggerRequest = CreateTriggerRequest.builder().actions(action).build();
//            
//            logger.debug("createTriggerRequest={}", createTriggerRequest);
            
            return true;
        }
        logger.error("Tried to create table but it already exists, tableId={}", tableName);
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
}
