package no.bibsys.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import no.bibsys.db.structures.Entity;

public class EntityManager {

    private final transient DynamoDBMapper mapper;
    private final transient TableDriver tableDriver;
    private final static Logger logger = LoggerFactory.getLogger(EntityManager.class);

    private EntityManager(AmazonDynamoDB client) {
        this.tableDriver = TableDriver.create(client);
        this.mapper = new DynamoDBMapper(client);
    }

    public Entity addEntity(Entity entity) {
        // TODO Auto-generated method stub
        return null;
    }

    public Entity getEntity(String entityId) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
