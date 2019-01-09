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
import no.bibsys.db.exceptions.SchemaTableBeingCreatedException;
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

    public Registry createRegistry(String validationSchemaTableName, Registry registry) throws SchemaTableBeingCreatedException {
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
            throw new RegistryNotFoundException(registry.getId());
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
            throw new RegistryNotFoundException(validationSchemaTableName); 
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

    private void checkIfSchemaTableExistsOrCreate(String schemaTable) throws SchemaTableBeingCreatedException {
        if (!tableDriver.tableExists(schemaTable)) {
            logger.info("Schema table does not exist, creating new one, schemaTable={}", schemaTable);
            tableDriver.createRegistryMetadataTable(schemaTable);
            try {
                validateRegistryExists(schemaTable);
                String schemaTableStatus = validateRegistryExists(schemaTable);
                logger.info(String.format("Schema table status: %s", schemaTableStatus));
            }catch(RegistryUnavailableException | RegistryNotFoundException e ) {
                logger.info("SchemaTable not finished initializing");
                throw new SchemaTableBeingCreatedException();
            }
        }else {
            try {
                validateRegistryExists(schemaTable);
            }catch(RegistryUnavailableException | RegistryNotFoundException e ) {
                throw new SchemaTableBeingCreatedException();
            }
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

    public void deleteRegistry(String validationSchemaTableName, String registryId) {

        logger.info("Deleting registry, registryId={}", registryId);

        validateRegistryExists(registryId);
        // disabled until we have a way to empty registries asynchronous
//        validateRegistryNotEmpty(registryId);
        tableDriver.deleteTable(registryId);
        Registry registry = getRegistry(validationSchemaTableName, registryId);
        
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(validationSchemaTableName))
                .build();
        
        try {
            mapper.delete(registry, config);
        } catch (ResourceNotFoundException e) {
            logger.info("Registry not found, registryId={}", registryId);
            throw new RegistryNotFoundException(registryId);
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

    public Registry uppdateRegistrySchema(String validationSchemaTableName, String registryId, String schema) {
        validateSchemaTable(validationSchemaTableName);
        validateRegistryNotEmpty(registryId);
        
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withSaveBehavior(SaveBehavior.UPDATE)
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(validationSchemaTableName))
                .build();
        
        try {
            // We only want to modify the schema
            Registry registry = getRegistry(validationSchemaTableName, registryId);
            registry.setSchema(schema);
            mapper.save(registry, config);
            
            logger.info("Registry schema updated successfully, validationSchemaTableNameId={}, registryId={}", validationSchemaTableName,
                    registry.getId());
            return registry;
        } catch (ResourceNotFoundException e) {
            throw new RegistryNotFoundException(registryId, validationSchemaTableName);           
        }
    }
    
    public Registry updateRegistryMetadata(String validationSchemaTableName, Registry registry) {
        validateSchemaTable(validationSchemaTableName);
        
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withSaveBehavior(SaveBehavior.UPDATE)
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(validationSchemaTableName))
                .build();

        try {
        
            // We don't want to update schema
            Registry existingRegistry = getRegistry(validationSchemaTableName, registry.getId());
            registry.setSchema(existingRegistry.getSchema());
            
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
