package service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import no.bibsys.LocalDynamoDBHelper;
import no.bibsys.MockEnvironmentReader;
import no.bibsys.service.ApiKey;
import no.bibsys.service.AuthenticationService;
import no.bibsys.web.security.Roles;

public class AuthenticationServiceTest {
    
    private AuthenticationService authenticationService;
    private DynamoDB dynamoDB;
    
    @BeforeClass
    public static void init() {
        System.setProperty("sqlite4java.library.path", "build/libs");
    }
    
    @Before
    public void setUp() throws Exception {
        AmazonDynamoDB client = LocalDynamoDBHelper.getClient();
        authenticationService = new AuthenticationService(client, new MockEnvironmentReader());
        dynamoDB = new DynamoDB(client);
    }
    
    @After
    public void tearDown() throws Exception {
        authenticationService.deleteApiKeyTable();
    }
    
    @Test
    public void tableIsCreatedAndDeleted() throws Exception {
        String tableName = authenticationService.createApiKeyTable();
        
        Table table = dynamoDB.getTable(tableName);
        TableDescription tableDescription = table.describe();
        
        
        Assert.assertNotNull("Expected table to be found", tableDescription);
        
        authenticationService.deleteApiKeyTable();
        
        Table deletedTable = dynamoDB.getTable(tableName);
        try {
            deletedTable.describe();
        } catch (Exception e) {
            Assert.assertTrue("Expected table to not be found", e instanceof ResourceNotFoundException);
        }        
    }
    
    @Test
    public void createApiAdminApiKey() throws Exception {
        authenticationService.createApiKeyTable();
        String apiKeyKey = authenticationService.saveApiKey(ApiKey.createApiAdminApiKey());
        
        ApiKey apiKey = authenticationService.getApiKey(apiKeyKey);
        
        Assert.assertEquals(Roles.API_ADMIN,apiKey.getRole());
    }
    
    @Test
    public void createRegistryAdminApiKey() throws Exception {
        authenticationService.createApiKeyTable();
        String apiKeyKey = authenticationService.saveApiKey(ApiKey.createRegistryAdminApiKey(null));
        
        ApiKey apiKey = authenticationService.getApiKey(apiKeyKey);
        
        Assert.assertEquals(Roles.REGISTRY_ADMIN, apiKey.getRole());
    }
    
    @Test
    public void testApiKeysShouldNotBeAutogeneratedByDB() throws Exception {
        authenticationService.createApiKeyTable();
        String key = "test";
        
        ApiKey apiKey = ApiKey.createApiAdminApiKey();
        apiKey.setKey(key);
        authenticationService.saveApiKey(apiKey);
        Assert.assertEquals(key, apiKey.getKey());
    }


}
