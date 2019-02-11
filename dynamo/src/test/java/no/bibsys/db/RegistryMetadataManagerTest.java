package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import no.bibsys.db.exceptions.RegistryNotFoundException;
import no.bibsys.db.structures.Registry;
import org.junit.Test;

public class RegistryMetadataManagerTest extends LocalDynamoTest {

    @Test(expected = RegistryNotFoundException.class)
    public void addRegistryToRegistryMetadataTable() {
        RegistryMetadataManager manager =
                new RegistryMetadataManager(newTableDriver(), new DynamoDBMapper(localClient));
        Registry randomRegistry = sampleData.sampleRegistry("aRegistry");
        manager.addRegistryToRegistryMetadataTable(registryMetadataTableName, randomRegistry);
    }

}
