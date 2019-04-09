package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import no.bibsys.db.exceptions.RegistryNotFoundException;
import no.bibsys.db.structures.Registry;

public class RegistryMetadataManager {

    private final transient TableDriver tableDriver;
    private final transient DynamoDBMapper mapper;

    public RegistryMetadataManager(TableDriver tableDriver, DynamoDBMapper mapper) {
        this.mapper = mapper;
        this.tableDriver = tableDriver;
    }

    public void addRegistryToRegistryMetadataTable(String registryMetadataTableName, Registry registry) {
        validateRegistryMetadataTable(registryMetadataTableName);
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder().withSaveBehavior(SaveBehavior.PUT)
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(registryMetadataTableName)).build();
        mapper.save(registry, config);

    }

    protected void validateRegistryMetadataTable(String registryMetadataTableName) {
        if (!tableDriver.tableExists(registryMetadataTableName)) {
            throw new RegistryNotFoundException(registryMetadataTableName);
        }
    }
}
