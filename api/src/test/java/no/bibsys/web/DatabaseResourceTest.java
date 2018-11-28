package no.bibsys.web;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.s3.Headers;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.bibsys.EnvironmentReader;
import no.bibsys.JerseyConfig;
import no.bibsys.LocalDynamoDBHelper;
import no.bibsys.MockEnvironmentReader;
import no.bibsys.db.Entity;
import no.bibsys.db.TableDriver;
import no.bibsys.db.structures.EntityRegistryTemplate;
import no.bibsys.service.ApiKey;
import no.bibsys.service.AuthenticationService;
import no.bibsys.testtemplates.SampleData;
import no.bibsys.web.model.CreatedRegistry;
import no.bibsys.web.security.ApiKeyConstants;


public class DatabaseResourceTest extends JerseyTest {

    private final SampleData sampleData = new SampleData();
    private String apiAdminKey;
    private String registryAdminKey;

    @Override
    protected Application configure() {
        AmazonDynamoDB client = LocalDynamoDBHelper.getClient();
        EnvironmentReader environmentReader = new MockEnvironmentReader();


        TableDriver tableDriver = TableDriver.create(client);
        List<String> listTables = tableDriver.listTables();

        listTables.forEach(tableDriver::deleteTable);

        AuthenticationService authenticationService =
                new AuthenticationService(client, environmentReader);
        authenticationService.createApiKeyTable();


        apiAdminKey = authenticationService.saveApiKey(ApiKey.createApiAdminApiKey());
        registryAdminKey = authenticationService.saveApiKey(ApiKey.createRegistryAdminApiKey(null));

        return new JerseyConfig(client, environmentReader);
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
        String registryName = UUID.randomUUID().toString();
        CreatedRegistry expected = new CreatedRegistry(
                String.format("A registry with name=%s is being created", registryName));

        Response response = createRegistry(registryName);
        CreatedRegistry entity = response.readEntity(CreatedRegistry.class);

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
        assertThat(entity.getMessage(), is(equalTo(expected.getMessage())));
        Assert.assertNotNull(entity.getApiKey());
    }


    @Test
    public void createRegistry_RegistryAlreadyExists_ReturnsStatusConflict() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName);
        Response response = createRegistry(registryName);
        assertThat(response.getStatus(), is(equalTo(Status.CONFLICT.getStatusCode())));
    }

    @Test
    public void insertEntity_RegistryExist_ReturnsStatusOK() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName);

        Entity entity = sampleData.sampleEntity();
        Response response = insertEntryRequest(registryName, entity.getBodyAsJson());
        Entity readEntity = response.readEntity(Entity.class);

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        Response readResponse = readEntity(registryName, readEntity.getId());
        assertThat(readResponse.getStatus(), is(equalTo(Status.OK.getStatusCode())));
    }

    @Test
    public void deleteRegistry_RegistryExists_ReturnsStatusOK() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName);

        Response response = target("/registry/" + registryName).request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).delete();

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
        String entity = response.readEntity(String.class);
        String expected = String.format("Registry %s has been deleted", registryName);
        assertThat(entity, is(equalTo(expected)));

    }


    @Test
    public void deleteRegistry_RegistryNotExisting_ReturnsStatusNotFound() throws Exception {

        String registryName = UUID.randomUUID().toString();
        Response response = target("/registry/" + registryName).request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).delete();

        String entity = response.readEntity(String.class);
        assertThat(response.getStatus(), is(equalTo(Status.NOT_FOUND.getStatusCode())));

        String expected = String.format("Registry with name %s does not exist", registryName);
        assertThat(entity, is(equalTo(expected)));

    }


    @Test
    public void emptyRegistry_RegistryExists_ReturnsStatusOK() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName);
        Entity entity = sampleData.sampleEntity();

        insertEntryRequest(registryName, entity.getBodyAsJson());

        Response response = target(String.format("/registry/%s/empty", registryName)).request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).delete();

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
    }

    @Test
    public void callEndpoint_WrongRole_ReturnsStatusForbidden() throws Exception {
        String registryName = UUID.randomUUID().toString();
        EntityRegistryTemplate request = new EntityRegistryTemplate(registryName);
        Response response = target("/registry").request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, registryAdminKey)
                .post(javax.ws.rs.client.Entity.entity(request, MediaType.APPLICATION_JSON));


        assertThat(response.getStatus(), is(equalTo(Status.FORBIDDEN.getStatusCode())));

    }

    @Test
    public void getRegistryMetadata_RegistryExists_ReturnsMetadata() throws Exception {

        String registryName = UUID.randomUUID().toString();
        EntityRegistryTemplate template = new EntityRegistryTemplate(registryName);

        ObjectMapper mapper = new ObjectMapper();
        String templateJson = mapper.writeValueAsString(template);

        createRegistry(template);

        Response response = target(String.format("/registry/%s", registryName)).request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).get();

        String entity = response.readEntity(String.class);

        assertThat(entity, is(equalTo(templateJson)));
    }

    @Test
    public void getEntity_RegistryExists_ReturnsStatusOK() throws Exception {
        String registryName = UUID.randomUUID().toString();
        EntityRegistryTemplate template = new EntityRegistryTemplate(registryName);
        createRegistry(template);

        Entity entity = sampleData.sampleEntity();
        Response response = insertEntryRequest(registryName, entity.getBodyAsJson());

        Entity readEntity = response.readEntity(Entity.class);

        Response readEntityResponse = readEntity(registryName, readEntity.getId());
        assertThat(readEntityResponse.getStatus(), is(equalTo(Status.OK.getStatusCode())));
        
        Assert.assertNotNull(readEntityResponse.getEntityTag());
        Assert.assertNotNull(readEntityResponse.getHeaderString(Headers.LAST_MODIFIED));

    }

    @Test
    public void getRegistryStatus_registryExists_returnsStatusCreated() {
        String registryName = UUID.randomUUID().toString();
        EntityRegistryTemplate template = new EntityRegistryTemplate(registryName);
        createRegistry(template);

        Response response = registryStatus(registryName);
        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
    }

    @Test
    public void putRegistrySchema_RegsitryExists_ReturnsStatusOK() throws Exception {
        String registryName = UUID.randomUUID().toString();
        EntityRegistryTemplate template = new EntityRegistryTemplate(registryName);
        createRegistry(template);

        Entity entity = sampleData.sampleEntity();
        insertEntryRequest(registryName, entity.getBodyAsJson());

        String schemaAsJson = "Schema as Json";
        Response putRegistrySchemaResponse = putSchema(registryName, schemaAsJson);
        assertThat(putRegistrySchemaResponse.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        Response response = readSchema(registryName);
        String schema = response.readEntity(String.class);
        assertThat(schema, is(equalTo(schemaAsJson)));
    }

    @Test
    public void updateEntity_EntityExists_ReturnsUpdatedEntity() throws Exception {

        String registryName = UUID.randomUUID().toString();
        EntityRegistryTemplate template = new EntityRegistryTemplate(registryName);
        createRegistry(template);

        Entity entity = sampleData.sampleEntity();
        Response writeResponse = insertEntryRequest(registryName, entity.getBodyAsJson());
        Entity writeEntity = writeResponse.readEntity(Entity.class);


        SampleData updatedSampleData = new SampleData();

        Entity updatedEntity = updatedSampleData.sampleEntity();
        updatedEntity.getBody().remove("label");
        String updatedLabel = "An updated label";
        updatedEntity.getBody().put("label", updatedLabel);

        Response response = updateEntityRequest(registryName, writeEntity.getId(),
                updatedEntity.getBodyAsJson());
        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        Response readEntityResponse = readEntity(registryName, writeEntity.getId());
        Entity readEntity = readEntityResponse.readEntity(Entity.class);
        String actual = readEntity.getBody().get("label").asText();
        assertThat(actual, is(equalTo(updatedLabel)));

    }

    private Response insertEntryRequest(String registryName, String jsonBody) {
        String path = String.format("/registry/%s/entity", registryName);
        return target(path).request().header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
                .post(javax.ws.rs.client.Entity.entity(jsonBody, MediaType.APPLICATION_JSON));

    }


    private Response createRegistry(String registryName) throws Exception {
        EntityRegistryTemplate createRequest = new EntityRegistryTemplate(registryName);
        return createRegistry(createRequest);
    }


    private Response createRegistry(EntityRegistryTemplate request) {
        Response response = target("/registry").request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
                .post(javax.ws.rs.client.Entity.entity(request, MediaType.APPLICATION_JSON));
        return response;
    }

    private Response registryStatus(String registryName) {
        Response response =
                target(String.format("/registry/%s/status", registryName)).request().get();
        return response;
    }

    private Response readEntity(String registryName, String entityId) throws Exception {
        return target(String.format("/registry/%s/entity/%s", registryName, entityId)).request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).get();
    }

    private Response putSchema(String registryName, String schemaAsJson) throws Exception {
        return target(String.format("/registry/%s/schema", registryName)).request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, registryAdminKey)
                .put(javax.ws.rs.client.Entity.entity(schemaAsJson, MediaType.APPLICATION_JSON));
    }

    private Response readSchema(String registryName) throws Exception {
        return target(String.format("/registry/%s/schema", registryName)).request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).get();
    }

    private Response updateEntityRequest(String registryName, String entityId, String jsonBody) {
        String path = String.format("/registry/%s/entity/%s", registryName, entityId);
        return target(path).request().header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
                .put(javax.ws.rs.client.Entity.entity(jsonBody, MediaType.APPLICATION_JSON));

    }

    private Response deleteEntity(String registryName, String entityId) {

        return target(String.format("/registry/%s/entity/%s", registryName, entityId)).request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).delete();
    }

    private Response entityStatus(String registryName, String entityId) {
        Response response =
                target(String.format("/registry/%s/entity/%s/status", registryName, entityId))
                        .request().get();
        return response;
    }
}
