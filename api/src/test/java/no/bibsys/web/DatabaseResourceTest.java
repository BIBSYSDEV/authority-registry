package no.bibsys.web;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Test;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.bibsys.EnvironmentReader;
import no.bibsys.JerseyConfig;
import no.bibsys.LocalDynamoDBHelper;
import no.bibsys.MockEnvironmentReader;
import no.bibsys.db.TableDriver;
import no.bibsys.db.structures.EntityRegistryTemplate;
import no.bibsys.service.ApiKey;
import no.bibsys.service.AuthenticationService;
import no.bibsys.testtemplates.SampleData;
import no.bibsys.testtemplates.SampleData.Entry;
import no.bibsys.web.model.SimpleResponse;
import no.bibsys.web.security.ApiKeyConstants;


public class DatabaseResourceTest extends JerseyTest {

    private final SampleData sampleData = new SampleData();
    private String apiAdminKey;
    private String registryAdminKey;
    
    @Override
    protected Application configure() {
        AmazonDynamoDB client = LocalDynamoDBHelper.getClient();
        EnvironmentReader environmentReader = new MockEnvironmentReader();
        
        JerseyConfig config = new JerseyConfig(client, environmentReader);
        
        TableDriver tableDriver = TableDriver.create(client, new DynamoDB(client));
        List<String> listTables = tableDriver.listTables();
        
        listTables.forEach(tableDriver::deleteTable);
        
        AuthenticationService authenticationService = new AuthenticationService(client, environmentReader);
        authenticationService.createApiKeyTable();
        
        
        apiAdminKey = authenticationService.saveApiKey(ApiKey.createApiAdminApiKey());    
        registryAdminKey = authenticationService.saveApiKey(ApiKey.createRegistryAdminApiKey());
        
        return config;
    }
    
    @BeforeClass
    public static void init() {
        System.setProperty("sqlite4java.library.path", "build/libs");
    }

    @Test
    public void ping_ReturnsStatusCodeOK() throws Exception {

        Response response = target("/ping").request().get();

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
    }


    @Test
    public void createRegistry_RegistryNotExisting_ReturnsStatusOK() throws Exception {
        String ragistryName = UUID.randomUUID().toString();
        SimpleResponse expected = new SimpleResponse(String.format("A registry with name %s has been created", ragistryName));

        SimpleResponse response = createRegistry(ragistryName).readEntity(SimpleResponse.class);

        assertThat(response.getStatusCode(), is(equalTo(Status.OK.getStatusCode())));
        assertThat(response, is(equalTo(expected)));
    }


    @Test
    public void createRegistry_RegistryAlreadyExists_ReturnsStatusCONFLICT() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName);
        SimpleResponse response = createRegistry(registryName).readEntity(SimpleResponse.class);
        assertThat(response.getStatusCode(), is(equalTo(Status.CONFLICT.getStatusCode())));
    }

    @Test
    public void insertEntity_RegistryExist_ReturnsStatusOK() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName);

        Entry entry = sampleData.sampleEntry();
        SimpleResponse response = insertEntryRequest(registryName, entry.jsonString()).readEntity(SimpleResponse.class);

        assertThat(response.getStatus(), is(equalTo(Status.OK)));
        String entityId = response.getMessage().substring(response.getMessage().lastIndexOf("/") + 1);

        SimpleResponse readResponse = readEntity(registryName, entityId).readEntity(SimpleResponse.class);
        assertThat(readResponse.getStatus(), is(equalTo(Status.OK)));
    }

    @Test
    public void deleteRegistry_RegistryExists_ReturnsStatusOK() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName );

        Response response = target("/registry/" + registryName)
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
                .delete();

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
        SimpleResponse actual = response.readEntity(SimpleResponse.class);
        SimpleResponse expected =
                new SimpleResponse(String.format("Registry %s has been deleted", registryName));
        assertThat(actual, is(equalTo(expected)));

    }


    @Test
    public void deleteRegistry_RegistryNotExisting_ReturnsStatusNOTFOUND() throws Exception {

        String registryName = UUID.randomUUID().toString();
        Response response = target("/registry/" + registryName)
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
                .delete();
        
        SimpleResponse readResponse = response.readEntity(SimpleResponse.class);
        assertThat(readResponse.getStatus(), is(equalTo(Status.NOT_FOUND)));

        SimpleResponse expected = new SimpleResponse(String.format("Registry with name %s does not exist", registryName), Status.NOT_FOUND);
        assertThat(readResponse, is(equalTo(expected)));

    }


    @Test
    public void emptyRegistry_RegistryExists_ReturnsStatusOK() throws Exception {
        String registryName =UUID.randomUUID().toString();
        createRegistry(registryName );
        String entry = sampleData.sampleEntry().jsonString();

        insertEntryRequest(registryName, entry);

        Response response = target(String.format("/registry/%s/empty", registryName))
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
                .delete();

        SimpleResponse actual = response.readEntity(SimpleResponse.class);
        assertThat(actual.getStatus(), is(equalTo(Status.OK)));
    }

    @Test
    public void callEndpoint_WrongRole_ReturnsStatusForbidden() throws Exception {
        String registryName = UUID.randomUUID().toString();
        EntityRegistryTemplate request = new EntityRegistryTemplate(registryName);
        Response response = target("/registry")
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, registryAdminKey)
                .post(Entity.entity(request, MediaType.APPLICATION_JSON));
        

        assertThat(response.getStatus(), is(equalTo(Status.FORBIDDEN.getStatusCode())));

    }

    @Test
    public void getRegistryMetadata_RegistryExists_ReturnsMetadata() throws Exception {

        String registryName = UUID.randomUUID().toString();
        EntityRegistryTemplate template = new EntityRegistryTemplate(registryName );

        ObjectMapper mapper = new ObjectMapper();
        String templateJson = mapper.writeValueAsString(template);

        createRegistry(template);

        Response response = target(String.format("/registry/%s", registryName))
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
                .get();

        SimpleResponse actual = response.readEntity(SimpleResponse.class);

        SimpleResponse expected = new SimpleResponse(templateJson, Status.OK);
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void getEntity_RegistryExists_ReturnsStatusOK() throws Exception {
        String registryName = UUID.randomUUID().toString();
        EntityRegistryTemplate template = new EntityRegistryTemplate(registryName);
        createRegistry(template);

        Entry entry = sampleData.sampleEntry();
        Response response = insertEntryRequest(registryName, entry.jsonString());

        String entityPath = response.readEntity(SimpleResponse.class).getMessage();
        String entityId = entityPath.substring(entityPath.lastIndexOf("/") + 1);

        Response readEntityResponse = readEntity(registryName, entityId);
        assertThat(readEntityResponse.getStatus(), is(equalTo(Status.OK.getStatusCode())));

    }

    @Test
    public void putRegistrySchema_RegsitryExists_ReturnsStatusOK() throws Exception {
        String registryName = UUID.randomUUID().toString();
        EntityRegistryTemplate template = new EntityRegistryTemplate(registryName);
        createRegistry(template);

        Entry entry = sampleData.sampleEntry();
        insertEntryRequest(registryName, entry.jsonString());

        String schemaAsJson = "Schema as Json";
        Response putRegistrySchemaResponse = putSchema(registryName, schemaAsJson);
        assertThat(putRegistrySchemaResponse.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        SimpleResponse actual = readSchema(registryName).readEntity(SimpleResponse.class);
        SimpleResponse expected = new SimpleResponse(schemaAsJson);
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void updateEntity_EntityExists_ReturnsUpdatedEntity() throws Exception {

        String registryName = UUID.randomUUID().toString();
        EntityRegistryTemplate template = new EntityRegistryTemplate(registryName);
        createRegistry(template);

        Entry entry = sampleData.sampleEntry();
        Response writeResponse = insertEntryRequest(registryName, entry.jsonString());
        SimpleResponse simpleResponse = writeResponse.readEntity(SimpleResponse.class);
        String path = simpleResponse.getMessage();
        String generatedId = path.substring(path.lastIndexOf("/") + 1);

        SampleData updatedSampleData = new SampleData();

        Entry updatedEntry = updatedSampleData.sampleEntry();
        updatedEntry.root.remove("label");
        String updatedLabel = "An updated label";
        updatedEntry.root.put("id", generatedId);
        updatedEntry.root.put("label", updatedLabel);

        String updatedJson = updatedEntry.jsonString();
        Response response = updateEntityRequest(registryName, generatedId, updatedJson);
        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        Response readEntityResponse = readEntity(registryName, generatedId);
        ObjectMapper objectMapper = new ObjectMapper();
        String actual = objectMapper.readTree(readEntityResponse.readEntity(SimpleResponse.class).getMessage()).get("label").asText();
        assertThat(actual, is(equalTo(updatedLabel)));

    }

    private Response insertEntryRequest(String registryName, String jsonBody) {
        String path = String.format("/registry/%s/entity", registryName);
        return target(path)
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
                .post(Entity.entity(jsonBody, MediaType.APPLICATION_JSON));

    }


    private Response createRegistry(String registryName) throws Exception {
        EntityRegistryTemplate createRequest = new EntityRegistryTemplate(registryName);
        return createRegistry(createRequest);
    }


    private Response createRegistry(EntityRegistryTemplate request) {
        Response response = target("/registry")
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
                .post(Entity.entity(request, MediaType.APPLICATION_JSON));
        return response;
    }

    private Response readEntity(String registryName, String entityId) throws Exception {
        return target(String.format("/registry/%s/entity/%s", registryName, entityId))
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
                .get();
    }

    private Response putSchema(String registryName, String schemaAsJson) throws Exception {
        return target(String.format("/registry/%s/schema", registryName))
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, registryAdminKey)
                .put(Entity.entity(schemaAsJson, MediaType.APPLICATION_JSON));
    }

    private Response readSchema(String registryName) throws Exception {
        return target(String.format("/registry/%s/schema", registryName))
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
                .get();
    }

    private Response updateEntityRequest(String registryName, String entityId, String jsonBody) {
        String path = String.format("/registry/%s/entity/%s", registryName, entityId);
        return target(path)
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
                .put(Entity.entity(jsonBody, MediaType.APPLICATION_JSON));

    }

    private Response deleteEntity(String registryName, String entityId) {

        return target(String.format("/registry/%s/entity/%s", registryName, entityId))
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
                .delete();
    }
}
