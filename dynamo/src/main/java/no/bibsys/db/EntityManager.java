package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPI;
import no.bibsys.db.exceptions.EntityNotFoundException;
import no.bibsys.db.exceptions.ItemExistsException;
import no.bibsys.db.exceptions.RegistryNotFoundException;
import no.bibsys.db.structures.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class EntityManager {

    private static final Logger logger = LoggerFactory.getLogger(EntityManager.class);
    private static final String ENTITY_EXISTS_MESSAGE = "Entity with id %s already exists";
    private final transient DynamoDBMapper mapper;
    private final transient TableDriver tableDriver;

    public EntityManager(AmazonDynamoDB client, AWSResourceGroupsTaggingAPI taggingApiClient, AWSLambda lambdaClient) {
        this.tableDriver = new TableDriver(client, taggingApiClient, lambdaClient);
        this.mapper = new DynamoDBMapper(client);
    }

    public Entity addEntity(String registryId, Entity entity) {
        checkRegistryExists(registryId);
        if (entityExists(registryId, entity.getId())) {
            throw new ItemExistsException(String.format(ENTITY_EXISTS_MESSAGE, entity.getId()));
        }

        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder().withSaveBehavior(SaveBehavior.PUT)
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryId)).build();

        mapper.save(entity, config);
        logger.info("Entity created successfully, registryId={}, entityId={}", registryId, entity.getId());
        return entity;

    }

    private void checkRegistryExists(String registryId) {
        if (!tableDriver.tableExists(registryId)) {
            throw new RegistryNotFoundException(registryId);
        }
    }

    public boolean deleteEntity(String registryId, String entityId) {
        checkRegistryExists(registryId);
        Entity entity = getEntity(registryId, entityId);
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryId)).build();
        mapper.delete(entity, config);
        return true;

    }

    public Entity getEntity(String registryId, String entityId) {
        checkRegistryExists(registryId);

        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryId)).build();
        Entity entity = mapper.load(Entity.class, entityId, config);
        if (Objects.nonNull(entity)) {
            return entity;
        } else {
            throw new EntityNotFoundException(registryId, entityId);
        }
    }

    public Entity updateEntity(String registryId, Entity entity) {
        checkRegistryExists(registryId);
        if (!entityExists(registryId, entity.getId())) {
            throw new EntityNotFoundException(registryId, entity.getId());
        }
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder().withSaveBehavior(SaveBehavior.UPDATE)
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryId)).build();
        mapper.save(entity, config);
        logger.info("Entity updated successfully, registryId={}, entityId={}", registryId, entity.getId());
        return entity;

    }

    public boolean entityExists(String registryId, String entityId) {
        try {
            getEntity(registryId, entityId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
