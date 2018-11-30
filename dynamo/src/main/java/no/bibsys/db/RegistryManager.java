package no.bibsys.db;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import no.bibsys.db.exceptions.RegistryAlreadyExistsException;
import no.bibsys.db.exceptions.RegistryNotEmptyException;
import no.bibsys.db.exceptions.RegistryNotFoundException;
import no.bibsys.db.exceptions.RegistryUnavailableException;
import no.bibsys.db.structures.Registry;

public class RegistryManager {

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

    public Registry createRegistry(String validationSchemaTableName, Registry registry) {
        checkIfSchemaTableExistsOrCreate(validationSchemaTableName);
        checkIfRegistryExistsInSchemaTable(validationSchemaTableName, registry.getId());
        return createRegistryTable(validationSchemaTableName, registry);
    }
    
    public Registry getRegistry(String validationSchemaTableName, String registryId) {
 
        validateSchemaTable(validationSchemaTableName);
        
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(validationSchemaTableName))
                .build();

        Registry registry = null;
        
        try {
            registry = mapper.load(Registry.class, registryId, config);
        } catch (ResourceNotFoundException e) {
            throw new RegistryNotFoundException(registryId, validationSchemaTableName);
        }
        
        if (registry != null) {
            return registry;
        } else {
            throw new RegistryNotFoundException(registryId, validationSchemaTableName);           
        }
    }

    private Registry createRegistryTable(String validationSchemaTable, Registry registry) {
        boolean created = tableDriver.createEntityRegistryTable(registry.getId());

        if (created) {
            addRegistryToSchemaTable(validationSchemaTable, registry);
            logger.info("Registry created successfully, registryId={}", registry.getId());
        } else {
            logger.error("Registry not created, registryId={}", registry.getId());
            //TODO: fix exception
        }
        return registry;
    }

    private Registry addRegistryToSchemaTable(String validationSchemaTableName, Registry registry) {
        
        validateSchemaTable(validationSchemaTableName);
        
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withSaveBehavior(SaveBehavior.PUT)
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(validationSchemaTableName))
                .build();
        
        try {
            mapper.save(registry, config);
            return registry;
        } catch (ResourceNotFoundException e) {
            throw new RegistryNotFoundException(validationSchemaTableName); //TODO: fix exception            
        }
        
    }

    private void validateSchemaTable(String validationSchemaTableName) {
        if (!tableDriver.tableExists(validationSchemaTableName)) {
            throw new RegistryNotFoundException(validationSchemaTableName); //TODO: fix exception
        } 
    }

    private void checkIfRegistryExistsInSchemaTable(String validationSchemaTableName, String registryId) {
        if (registryExists(validationSchemaTableName, registryId)) {
            String message = String.format(
                    "Registry already exists in schema table, registryId=%s, schemeTable=%s",
                    registryId, registryId);
            throw new RegistryAlreadyExistsException(message);
        }
    }

    private void checkIfSchemaTableExistsOrCreate(String schemaTable) {
        if (!tableDriver.tableExists(schemaTable)) {
            logger.info(
                    "Schema table does not exist, creating new one, schemaTable={}", schemaTable);
            tableDriver.createRegistryMetadataTable(schemaTable);
        }
    }

    public boolean registryExists(String validationSchemaTableName, String registryId) {
        try {
            getRegistry(validationSchemaTableName, registryId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String validateRegistryExists(String registryName) {
    	RegistryStatus status = status(registryName);
    	switch(status) {
        case ACTIVE:
            return "CREATED";
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
        validateRegistryExists(registryId);
        if (tableDriver.tableSize(registryId) > 0) {
            logger.warn("Registry is not empty, registryId={}", registryId);
            throw new RegistryNotEmptyException(registryId);
        }        
    }

    public void deleteRegistry(String validationSchemaTableName, String registryId) {

        logger.info("Deleting registry, registryId={}", registryId);

        validateRegistryNotEmpty(registryId);
        tableDriver.deleteTable(registryId);
        Registry registry = getRegistry(validationSchemaTableName, registryId);
        
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(validationSchemaTableName))
                .build();
        
        try {
            mapper.delete(registry, config);
        } catch (ResourceNotFoundException e) {
            logger.info("Registry not found, registryId={}", registryId);
            //TODO: fix exception
        }
    }

    public List<String> getRegistries(String validationSchemaTableName) {
        
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withSaveBehavior(SaveBehavior.PUT)
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(validationSchemaTableName))
                .build();
        
        List<String> tables = tableDriver.listTables();
        return tables.stream()
                .filter(tableName -> mapper.load(Registry.class, tableName, config) != null)
                .collect(Collectors.toList());
    }

    public Registry updateRegistry(String validationSchemaTableName, Registry registry) {
        validateSchemaTable(validationSchemaTableName);
        validateRegistryNotEmpty(registry.getId());
        
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withSaveBehavior(SaveBehavior.UPDATE)
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(validationSchemaTableName))
                .build();

        try {
            mapper.save(registry, config);
            logger.info("Registry metadata updated successfully, validationSchemaTableNameId={}, registryId={}", validationSchemaTableName,
                    registry.getId());  
            return registry;
        } catch (ResourceNotFoundException e) {
            throw new RegistryNotFoundException(registry.getId(), validationSchemaTableName);           
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
