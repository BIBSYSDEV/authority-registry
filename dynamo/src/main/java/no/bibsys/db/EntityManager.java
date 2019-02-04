package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import java.util.Objects;
import no.bibsys.db.exceptions.EntityNotFoundException;
import no.bibsys.db.exceptions.RegistryNotFoundException;
import no.bibsys.db.structures.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityManager {

    private static final Logger logger = LoggerFactory.getLogger(EntityManager.class);
    private final transient DynamoDBMapper mapper;
    private final transient TableDriver tableDriver;

    public EntityManager(AmazonDynamoDB client) {
        this.tableDriver = TableDriver.create(client);
        this.mapper = new DynamoDBMapper(client);
    }

    public Entity addEntity(String registryId, Entity entity) {
        validateRegistry(registryId);

        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder().withSaveBehavior(SaveBehavior.PUT)
            .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryId)).build();

        try {
            mapper.save(entity, config);
            logger.info("Entity created successfully, registryId={}, entityId={}", registryId, entity.getId());
            return entity;
        } catch (ResourceNotFoundException e) {
            throw new RegistryNotFoundException(registryId);
        }
    }

    /**
     * Method to validate a registry and if it exists as a DynamoDB table on AWS.
     */
    private void validateRegistry(String registryId) {
        if (!tableDriver.tableExists(registryId)) {
            throw new RegistryNotFoundException(registryId);
        }
    }

    public boolean deleteEntity(String registryId, String entityId) {
        validateRegistry(registryId);

        Entity entity = getEntity(registryId, entityId);

        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
            .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryId)).build();

        try {
            mapper.delete(entity, config);
            return true;
        } catch (ResourceNotFoundException e) {
            throw new EntityNotFoundException(registryId, entityId);
        }
    }

    public Entity getEntity(String registryId, String entityId) {
        validateRegistry(registryId);

        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
            .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryId)).build();

        Entity entity;

        try {
            entity = mapper.load(Entity.class, entityId, config);
        } catch (ResourceNotFoundException e) {
            throw new EntityNotFoundException(registryId, entityId);
        }

        if (Objects.nonNull(entity)) {
            return entity;
        } else {
            throw new EntityNotFoundException(registryId, entityId);
        }
    }

    public Entity updateEntity(String registryId, Entity entity) {
        validateRegistry(registryId);

        if (!entityExists(registryId, entity.getId())) {
            throw new EntityNotFoundException(registryId, entity.getId());
        }

        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder().withSaveBehavior(SaveBehavior.UPDATE)
            .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryId)).build();

        try {
            mapper.save(entity, config);
            logger.info("Entity updated successfully, registryId={}, entityId={}", registryId, entity.getId());
            return entity;
        } catch (ResourceNotFoundException e) {
            throw new EntityNotFoundException(registryId, entity.getId());
        }
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
