package no.bibsys.testtemplates;

import org.junit.Before;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import no.bibsys.LocalDynamoDBHelper;
import no.bibsys.MockEnvironmentReader;
import no.bibsys.db.EntityManager;
import no.bibsys.db.RegistryManager;
import no.bibsys.service.AuthenticationService;
import no.bibsys.service.EntityService;
import no.bibsys.service.RegistryService;

public abstract class LocalDynamoTest {

    protected RegistryService registryService;
    protected EntityService entityService;
    protected AuthenticationService authenticationService;
    public SampleData sampleData;

    @Before
    public void setUp() {
        System.setProperty("sqlite4java.library.path", "build/libs");

        final AmazonDynamoDB client = LocalDynamoDBHelper.getClient();


        authenticationService = new AuthenticationService(client, new MockEnvironmentReader());
        registryService = new RegistryService(new RegistryManager(client), authenticationService, new MockEnvironmentReader());
        entityService = new EntityService(new EntityManager(client));        
        
        sampleData = new SampleData();
    }
}
