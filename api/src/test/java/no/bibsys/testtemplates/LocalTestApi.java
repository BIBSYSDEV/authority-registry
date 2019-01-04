package no.bibsys.testtemplates;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import javax.ws.rs.core.Application;
import no.bibsys.JerseyConfig;
import no.bibsys.LocalDynamoDBHelper;
import no.bibsys.MockEnvironment;
import no.bibsys.db.EntityManager;
import no.bibsys.db.RegistryManager;
import no.bibsys.service.AuthenticationService;
import no.bibsys.service.EntityService;
import no.bibsys.service.RegistryService;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;

public abstract class LocalTestApi extends JerseyTest {

    public SampleData sampleData;
    protected transient AmazonDynamoDB client;
    protected RegistryService registryService;
    protected EntityService entityService;
    protected AuthenticationService authenticationService;


    public LocalTestApi() {
        super();
    }


    @Before
    public void setUp() {
        System.setProperty("sqlite4java.library.path", "build/libs");
        authenticationService = new AuthenticationService(client, new MockEnvironment());
        registryService = new RegistryService(new RegistryManager(client), authenticationService,
            new MockEnvironment());
        entityService = new EntityService(new EntityManager(client));
        sampleData = new SampleData();
    }


    // Called from the constructor because it is called by JerseysTest constructor.
    protected Application configure() {
        System.setProperty("sqlite4java.library.path", "build/libs");
        client = LocalDynamoDBHelper.getClient();
        return new JerseyConfig(client, new MockEnvironment());
    }


}
