package no.bibsys.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;

import no.bibsys.db.exceptions.EntityNotFoundException;
import no.bibsys.db.exceptions.RegistryNotFoundException;
import no.bibsys.db.structures.Entity;

public class EntityManager {

    private final transient DynamoDBMapper mapper;
    private final transient TableDriver tableDriver;
    private static final Logger logger = LoggerFactory.getLogger(EntityManager.class);

    public EntityManager(AmazonDynamoDB client) {
        this.tableDriver = TableDriver.create(client);
        this.mapper = new DynamoDBMapper(client);
    }

    public Entity addEntity(String registryId, Entity entity) {
        validateRegistry(registryId);
        
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withSaveBehavior(SaveBehavior.PUT)
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryId))
                .build();
        
        try {
            mapper.save(entity, config);
            logger.info("Entity created successfully, registryId={}, entityId={}", registryId,
                    entity.getId());
            return entity;
        } catch (ResourceNotFoundException e) {
            throw new RegistryNotFoundException(registryId);
        }
    }

    public Entity getEntity(String registryId, String entityId) {
        validateRegistry(registryId);
        
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryId))
                .build();
        
        Entity entity = null;
        
        try {
            entity = mapper.load(Entity.class, entityId, config);
        } catch (ResourceNotFoundException e) {
            throw new EntityNotFoundException(registryId, entityId);
        }
        
        if (entity != null) {
            return entity;
        } else {
            throw new EntityNotFoundException(registryId, entityId);
        }
    }

    public boolean deleteEntity(String registryId, String entityId) {
        validateRegistry(registryId);
        
        Entity entity = getEntity(registryId, entityId);
        
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryId))
                .build();
        
        try {
            mapper.delete(entity, config);
            return true;
        } catch (ResourceNotFoundException e) {
            throw new EntityNotFoundException(registryId, entityId);            
        }
    }

    public Entity updateEntity(String registryId, Entity entity) {
        validateRegistry(registryId);
        
        if (!entityExists(registryId, entity.getId())) {
            throw new EntityNotFoundException(registryId, entity.getId());
        }
        
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withSaveBehavior(SaveBehavior.UPDATE)
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryId))
                .build();

        try {
            mapper.save(entity, config);
            logger.info("Entity updated successfully, registryId={}, entityId={}", registryId,
                    entity.getId());  
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
    
    /**
     * Method to validate a registry and if it exists as a DynamoDB table on AWS.
     * @param registryId
     */
    private void validateRegistry(String registryId) {
        if (!tableDriver.tableExists(registryId)) {
            throw new RegistryNotFoundException(registryId);
        }
    }
    
}
