package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import no.bibsys.db.exceptions.RegistryNotFoundException;
import no.bibsys.db.structures.Registry;

public class RegistryMetadataManager {

    private final transient TableDriver tableDriver;
    private final transient DynamoDBMapper mapper;


    public RegistryMetadataManager(TableDriver tableDriver, DynamoDBMapper mapper) {
        this.mapper = mapper;
        this.tableDriver = tableDriver;
    }


    public Registry addRegistryToRegistryMetadataTable(String registryMetadataTableName,
        Registry registry) {

        validateRegistryMetadataTable(registryMetadataTableName);

        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
            .withSaveBehavior(SaveBehavior.PUT)
            .withTableNameOverride(
                TableNameOverride.withTableNameReplacement(registryMetadataTableName))
            .build();

        try {
            mapper.save(registry, config);
            return registry;
        } catch (ResourceNotFoundException e) {
            throw new RegistryNotFoundException(registryMetadataTableName); //TODO: fix exception
        }

    }


    protected void validateRegistryMetadataTable(String registryMetadataTableName) {
        if (!tableDriver.tableExists(registryMetadataTableName)) {
            throw new RegistryNotFoundException(registryMetadataTableName);
        }
    }


}
