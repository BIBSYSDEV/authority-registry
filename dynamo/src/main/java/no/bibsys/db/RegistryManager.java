package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import no.bibsys.db.exceptions.RegistryAlreadyExistsException;
import no.bibsys.db.exceptions.RegistryNotEmptyException;
import no.bibsys.db.exceptions.RegistryNotFoundException;
import no.bibsys.db.exceptions.RegistryUnavailableException;
import no.bibsys.db.exceptions.RegistryMetadataTableBeingCreatedException;
import no.bibsys.db.structures.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistryManager {

    private static final String TABLE_CREATED = "CREATED";

    public enum RegistryStatus {
        CREATING, UPDATING, DELETING, ACTIVE, NOT_FOUND;
    }

    private final transient TableDriver tableDriver;
    private final transient DynamoDBMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(RegistryManager.class);

    public RegistryManager(AmazonDynamoDB client) {
        this.tableDriver = TableDriver.create(client);
        this.mapper = new DynamoDBMapper(client);

    }

    public Registry createRegistry(String registryMetadataTableName, Registry registry) throws RegistryMetadataTableBeingCreatedException {
        checkIfRegistryMetadataTableExistsOrCreate(registryMetadataTableName);
        checkIfRegistryExistsInRegistryMetadataTable(registryMetadataTableName, registry.getId());
        return createRegistryTable(registryMetadataTableName, registry);
    }
    
    public Registry getRegistry(String registryMetadataTableName, String registryId) {
 
        validateRegistryeMetadataTable(registryMetadataTableName);
        
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryMetadataTableName))
                .build();

        Registry registry = null;
        
        try {
            registry = mapper.load(Registry.class, registryId, config);
        } catch (ResourceNotFoundException e) {
            throw new RegistryNotFoundException(registryId, registryMetadataTableName);
        }
        
        if (registry != null) {
            return registry;
        } else {
            throw new RegistryNotFoundException(registryId, registryMetadataTableName);           
        }
    }

    private Registry createRegistryTable(String registryMetadataTable, Registry registry) {
        boolean created = tableDriver.createEntityRegistryTable(registry.getId());

        if (created) {
            addRegistryToRegistryMetadataTable(registryMetadataTable, registry);
            logger.info("Registry created successfully, registryId={}", registry.getId());
        } else {
            logger.error("Registry not created, registryId={}", registry.getId());
            throw new RegistryNotFoundException(registry.getId());
        }
        return registry;
    }

    private Registry addRegistryToRegistryMetadataTable(String registryMetadataTableName, Registry registry) {
        
        validateRegistryeMetadataTable(registryMetadataTableName);
        
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withSaveBehavior(SaveBehavior.PUT)
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryMetadataTableName))
                .build();
        
        try {
            mapper.save(registry, config);
            return registry;
        } catch (ResourceNotFoundException e) {
            throw new RegistryNotFoundException(registryMetadataTableName); //TODO: fix exception            
        }
        
    }

    private void validateRegistryeMetadataTable(String registryMetadataTableName) {
        if (!tableDriver.tableExists(registryMetadataTableName)) {
            throw new RegistryNotFoundException(registryMetadataTableName); 
        } 
    }

    private void checkIfRegistryExistsInRegistryMetadataTable(String registryMetadataTableName, String registryId) {
        if (registryExists(registryMetadataTableName, registryId)) {
            String message = String.format(
                    "Registry already exists in metadata table, registryId=%s, schemeTable=%s",
                    registryId, registryId);
            throw new RegistryAlreadyExistsException(message);
        }
    }

    private void checkIfRegistryMetadataTableExistsOrCreate(String metadataTable) throws RegistryMetadataTableBeingCreatedException {
        if (!tableDriver.tableExists(metadataTable)) {
            logger.info("Registry metadata table does not exist, creating new one, metadataTable={}", metadataTable);
            tableDriver.createRegistryMetadataTable(metadataTable);
        }
        try {
            validateRegistryExists(metadataTable);
        } catch(RegistryUnavailableException | RegistryNotFoundException e ) {
            logger.info("Registry metadata table not finished initializing");
            throw new RegistryMetadataTableBeingCreatedException();
        }
    }

    public boolean registryExists(String registryMetadataTableName, String registryId) {
        try {
            getRegistry(registryMetadataTableName, registryId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String validateRegistryExists(String registryName) {
    	RegistryStatus status = status(registryName);
    	switch(status) {
        case ACTIVE:
            return TABLE_CREATED;
        case CREATING:
        case UPDATING:
            throw new RegistryUnavailableException(registryName, status.name().toLowerCase(Locale.ENGLISH));
        case DELETING:
        case NOT_FOUND:
        default:
            throw new RegistryNotFoundException(registryName);
    	}
    }

    public void emptyRegistry(String tableName) {
        tableDriver.emptyEntityRegistryTable(tableName);
        tableDriver.createEntityRegistryTable(tableName);
    }
    
    public void validateRegistryNotEmpty(String registryId) {
        if (!tableDriver.isTableEmpty(registryId)) {
            logger.warn("Registry is not empty, registryId={}", registryId);
            throw new RegistryNotEmptyException(registryId);
        }        
    }

    public void deleteRegistry(String registryMetadataTableName, String registryId) {

        logger.info("Deleting registry, registryId={}", registryId);

        validateRegistryExists(registryId);
        // disabled until we have a way to empty registries asynchronous
//        validateRegistryNotEmpty(registryId);
        tableDriver.deleteTable(registryId);
        Registry registry = getRegistry(registryMetadataTableName, registryId);
        
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryMetadataTableName))
                .build();
        
        try {
            mapper.delete(registry, config);
        } catch (ResourceNotFoundException e) {
            logger.info("Registry not found, registryId={}", registryId);
            throw new RegistryNotFoundException(registryId);
        }
    }

    public List<String> getRegistries(String registryMetadataTableName) {
        
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withSaveBehavior(SaveBehavior.PUT)
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryMetadataTableName))
                .build();
        
        List<String> tables = tableDriver.listTables();
        return tables.stream()
                .filter(tableName -> mapper.load(Registry.class, tableName, config) != null)
                .collect(Collectors.toList());
    }

    public Registry uppdateRegistrySchema(String registryMetadataTableName, String registryId, String schema) {
        validateRegistryeMetadataTable(registryMetadataTableName);
        validateRegistryNotEmpty(registryId);
        
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withSaveBehavior(SaveBehavior.UPDATE)
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryMetadataTableName))
                .build();
        
        try {
            // We only want to modify the schema
            Registry registry = getRegistry(registryMetadataTableName, registryId);
            registry.setSchema(schema);
            mapper.save(registry, config);
            
            logger.info("Registry schema updated successfully, registryMetadataTableNameId={}, registryId={}", registryMetadataTableName,
                    registry.getId());
            return registry;
        } catch (ResourceNotFoundException e) {
            throw new RegistryNotFoundException(registryId, registryMetadataTableName);           
        }
    }
    
    public Registry updateRegistryMetadata(String registryMetadataTableName, Registry registry) {
        validateRegistryeMetadataTable(registryMetadataTableName);
        
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withSaveBehavior(SaveBehavior.UPDATE)
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryMetadataTableName))
                .build();

        try {
        
            // We don't want to update schema
            Registry existingRegistry = getRegistry(registryMetadataTableName, registry.getId());
            registry.setSchema(existingRegistry.getSchema());
            
            mapper.save(registry, config);
            logger.info("Registry metadata updated successfully, registryMetadataTableNameId={}, registryId={}", registryMetadataTableName,
                    registry.getId());  
            return registry;
        } catch (ResourceNotFoundException e) {
            throw new RegistryNotFoundException(registry.getId(), registryMetadataTableName);           
        }
    }

    public RegistryStatus status(String registryName) {

        RegistryStatus registryStatus = RegistryStatus.valueOf(tableDriver.status(registryName));
        if (registryStatus == null) {
            registryStatus = RegistryStatus.NOT_FOUND;
        }

        return registryStatus;
    }

}
