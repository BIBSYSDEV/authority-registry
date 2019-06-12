package no.bibsys.web.exception;

import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPI;

import no.bibsys.LocalDynamoDBHelper;
import no.bibsys.MockEnvironment;
import no.bibsys.aws.tools.Environment;
import no.bibsys.db.TableDriver;
import no.bibsys.db.helpers.AwsLambdaMock;
import no.bibsys.db.helpers.AwsResourceGroupsTaggingApiMock;
import no.bibsys.service.ApiKey;
import no.bibsys.service.AuthenticationService;
import no.bibsys.service.EntityService;
import no.bibsys.service.RegistryService;
import no.bibsys.testtemplates.SampleData;
import no.bibsys.utils.IoUtils;
import no.bibsys.web.DatabaseResource;
import no.bibsys.web.model.EntityDto;
import no.bibsys.web.model.RegistryDto;
import no.bibsys.web.security.ApiKeyConstants;

public abstract class MapperTest<M extends ExceptionMapper<?>> extends JerseyTest {

    protected static final String VALIDATION_FOLDER = "validation";
    protected static final String INVALID_SHACL_VALIDATION_SCHEMA_JSON =
            "invalidDatatypeRangeShaclValidationSchema.json";
    protected static final String VALID_SHACL_VALIDATION_SCHEMA_JSON = "validShaclValidationSchema.json";
    protected static String validValidationSchema;
    protected final SampleData sampleData = new SampleData();

    protected String apiAdminKey;
    protected String registryAdminKey;
    protected RegistryService mockRegistryService;
    protected EntityService mockEntityService;

    @BeforeClass
    public static void init() throws IOException {
        System.setProperty("sqlite4java.library.path", "build/libs");
        validValidationSchema = IoUtils.resourceAsString(
                Paths.get(VALIDATION_FOLDER, VALID_SHACL_VALIDATION_SCHEMA_JSON));
    }

    @Override
    protected Application configure() {

        AmazonDynamoDB client = LocalDynamoDBHelper.getClient();
        Environment environmentReader = new MockEnvironment();
        AWSResourceGroupsTaggingAPI mockTaggingClient = AwsResourceGroupsTaggingApiMock.build(); 
        AWSLambda mockLambdaClient = AwsLambdaMock.build(); 

        TableDriver tableDriver = new TableDriver(client, mockTaggingClient, mockLambdaClient);
        List<String> listTables = tableDriver.listTables();

        listTables.forEach(tableDriver::deleteTable);

        AuthenticationService authenticationService = new AuthenticationService(client, environmentReader);
        authenticationService.createApiKeyTable();

        apiAdminKey = authenticationService.saveApiKey(ApiKey.createApiAdminApiKey());
        registryAdminKey = authenticationService.saveApiKey(ApiKey.createRegistryAdminApiKey(null));

        ResourceConfig jerseyConfig = new ResourceConfig();

        mockRegistryService = mock(RegistryService.class);
        mockEntityService = mock(EntityService.class);
        jerseyConfig.register(new DatabaseResource(mockRegistryService, mockEntityService));

        jerseyConfig.register(createMapper().getClass());

        return jerseyConfig;
    }

    protected abstract M createMapper();

    protected Response createRegistry(RegistryDto registryDto, String apiKey) {

        Response response = target("/registry").request().accept(MediaType.APPLICATION_JSON)
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiKey)
                .post(javax.ws.rs.client.Entity.entity(registryDto, MediaType.APPLICATION_JSON));
        return response;
    }

    protected Response insertEntityRequest(String registryName, EntityDto entityDto, String apiKey) {
        String path = String.format("/registry/%s/entity", registryName);
        return target(path).request().header(ApiKeyConstants.API_KEY_PARAM_NAME, apiKey)
                .post(javax.ws.rs.client.Entity.entity(entityDto, MediaType.APPLICATION_JSON));
    }

    protected Response putSchema(String registryName, String schemaAsJson) {
        return target(String.format("/registry/%s/schema", registryName)).request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, registryAdminKey)
                .put(javax.ws.rs.client.Entity.entity(schemaAsJson, MediaType.APPLICATION_JSON));
    }
}
