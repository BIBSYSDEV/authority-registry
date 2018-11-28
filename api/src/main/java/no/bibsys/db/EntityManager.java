package no.bibsys.db;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import no.bibsys.web.exception.EntityNotFoundException;
import no.bibsys.web.exception.RegistryNotFoundException;

public class EntityManager {

    private final transient ItemDriver itemDriver;
    private final transient DynamoDBMapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(EntityManager.class);

    public EntityManager(ItemDriver itemDriver, AmazonDynamoDB client) {
        this.itemDriver = itemDriver;
        mapper = new DynamoDBMapper(client);

    }

    public Entity addEntity(final String registryName, final String json)
            throws IOException {

        if (!itemDriver.tableExists(registryName)) {
            throw new RegistryNotFoundException(registryName);
        }
        
        Entity entity = new Entity(json);
        entity.validate();
        
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withSaveBehavior(SaveBehavior.PUT)
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryName))
                .build();
        
        try {
            mapper.save(entity, config);            
            logger.info("Entity created successfully, registryId={}, entityId={}", registryName,
                    entity.getId());
            return entity;            
        } catch (ResourceNotFoundException e) {
            throw new RegistryNotFoundException(registryName);
        }
    }

    public Entity getEntity(String registryName, String id) {
        if (!itemDriver.tableExists(registryName)) {
            throw new RegistryNotFoundException(registryName);
        }
        
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryName))
                .build();
        try {
            Entity entity = mapper.load(Entity.class, id, config);
            if (entity != null) {
                return entity;
            } else {
                throw new EntityNotFoundException(registryName, id);
            }            
        } catch (ResourceNotFoundException e) {
            throw new EntityNotFoundException(registryName, id);
        }

        
    }

    public boolean deleteEntity(String registryName, String entityId) {
        return itemDriver.deleteItem(registryName, entityId);

    }

    public Entity updateEntity(String registryName, String entityId, String json) {
        if (!itemDriver.tableExists(registryName)) {
            throw new RegistryNotFoundException(registryName);
        }
        if (!itemDriver.itemExists(registryName, entityId)) {
            throw new EntityNotFoundException(registryName, entityId);
        }
        
        Entity entity = new Entity(json);
        entity.setId(entityId);
        entity.validate();
        
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withSaveBehavior(SaveBehavior.UPDATE)
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryName))
                .build();
        
        mapper.save(entity, config);            
        logger.info("Entity updated successfully, registryId={}, entityId={}", registryName,
                entity.getId());
        return entity;            

    }

    public boolean entityExists(String registryName, String entityId) {
        return itemDriver.itemExists(registryName, entityId);
    }

    public Status validateItemExists(String registryName, String entityId) {
        if (!entityExists(registryName, entityId)) {
            throw new EntityNotFoundException(registryName, entityId);
        }

        return Status.CREATED;
    }
}
