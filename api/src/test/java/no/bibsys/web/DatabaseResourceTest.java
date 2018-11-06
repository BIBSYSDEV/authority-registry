package no.bibsys.web;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.bibsys.JerseyConfig;
import no.bibsys.LocalDynamoDBHelper;
import no.bibsys.MockEnvironmentReader;
import no.bibsys.db.DatabaseManager;
import no.bibsys.db.RegistryManager;
import no.bibsys.db.TableDriver;
import no.bibsys.db.structures.EntityRegistryTemplate;
import no.bibsys.testtemplates.SampleData;
import no.bibsys.testtemplates.SampleData.Entry;
import no.bibsys.web.model.SimpleResponse;
import no.bibsys.web.security.ApiKeyConstants;


public class DatabaseResourceTest extends JerseyTest {

    private final SampleData sampleData = new SampleData();

    @Override
    protected Application configure() {
        TableDriver tableDriver = LocalDynamoDBHelper.getTableDriver();
        TableCollection<ListTablesResult> listTables = tableDriver.getDynamoDb().listTables();
        
        listTables.forEach(table -> {
            table.delete();
            try {
                table.waitForDelete();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        JerseyConfig config = new JerseyConfig(new DatabaseManager(tableDriver), new RegistryManager(tableDriver), new MockEnvironmentReader());
        return config;
    }

    @BeforeClass
    public static void init() {
        System.setProperty("sqlite4java.library.path", "build/libs");
    }

    @Test
    public void pingReturnsStatusCodeOK() throws Exception {

        Response response = target("/ping").request().get();

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
    }


    @Test
    public void sendSuccessWhenCreatingNonExistingTable() throws Exception {
        String tableName = UUID.randomUUID().toString();
        SimpleResponse expected = new SimpleResponse(String.format("A registry with name %s has been created", tableName));

        Response response = createTable(tableName);

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        SimpleResponse actual = response.readEntity(SimpleResponse.class);
        assertThat(actual, is(equalTo(expected)));
    }


    @Test
    public void sendConflictWhenCreatingExistingTable() throws Exception {
        String tableName = UUID.randomUUID().toString();
        createTable(tableName);
        Response response = createTable(tableName);
        assertThat(response.getStatus(), is(equalTo(Status.CONFLICT.getStatusCode())));
    }

    @Test
    public void insertEntryInTable() throws Exception {
        String tableName = UUID.randomUUID().toString();
        createTable(tableName);

        Entry entry = sampleData.sampleEntry("entryId");
        Response response = insertEntryRequest(tableName, entry.jsonString());

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
        SimpleResponse actual = response.readEntity(SimpleResponse.class);
        String entityId = actual.getMessage().substring(actual.getMessage().lastIndexOf("/") + 1);

        Response readResponse = readEntity(tableName, entityId);
        assertThat(readResponse.getStatus(), is(equalTo(Status.OK.getStatusCode())));
        
    }

    @Test
    public void deleteAnExistingRegistry() throws Exception {
        String tableName = UUID.randomUUID().toString();
        createTable(tableName );

        Response response = target("/registry/" + tableName)
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, MockEnvironmentReader.TEST_API_ADMIN_API_KEY)
                .delete();

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
        SimpleResponse actual = response.readEntity(SimpleResponse.class);
        SimpleResponse expected =
                new SimpleResponse(String.format("Registry %s has been deleted", tableName));
        assertThat(actual, is(equalTo(expected)));

    }


    @Test
    public void returnErrorWhenDeletingNonExistingRegistry() throws Exception {

        String tableName = UUID.randomUUID().toString();
        Response response = target("/registry/" + tableName)
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, MockEnvironmentReader.TEST_API_ADMIN_API_KEY)
                .delete();
        assertThat(response.getStatus(), is(equalTo(Status.NOT_FOUND.getStatusCode())));

        SimpleResponse actual = response.readEntity(SimpleResponse.class);
        SimpleResponse expected =
                new SimpleResponse("Table does not exist");
        assertThat(actual, is(equalTo(expected)));

    }


    @Test
    public void emptyAnExistingTable() throws Exception {
        String tableName =UUID.randomUUID().toString();
        createTable(tableName );
        String entry = sampleData.sampleEntry("entryId").jsonString();

        insertEntryRequest(tableName, entry);

        Response response = target(String.format("/registry/%s/empty", tableName))
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, MockEnvironmentReader.TEST_API_ADMIN_API_KEY)
                .delete();

        SimpleResponse actual = response.readEntity(SimpleResponse.class);
        SimpleResponse expected = new SimpleResponse(String.format("Registry %s has been emptied", tableName));
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void wrongRoleShouldReturnForbidden() throws Exception {
        String tableName = UUID.randomUUID().toString();
        EntityRegistryTemplate request = new EntityRegistryTemplate(tableName);
        Response response = target("/registry")
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, MockEnvironmentReader.TEST_REGISTRY_ADMIN_API_KEY)
                .post(Entity.entity(request, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus(), is(equalTo(Status.FORBIDDEN.getStatusCode())));

    }

    @Test 
    public void getListOfRegistries() throws Exception {
        String tableName = UUID.randomUUID().toString();
        EntityRegistryTemplate request = new EntityRegistryTemplate(tableName );
        createRegistry(request);

        Response response = target("/registry")
                .request()
                .get();

        SimpleResponse actual = response.readEntity(SimpleResponse.class);
        SimpleResponse expected = new SimpleResponse(String.format("[\"%s\"]", tableName), 200);
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void getRegistryMetadata() throws Exception {

        String tableName = UUID.randomUUID().toString();
        EntityRegistryTemplate template = new EntityRegistryTemplate(tableName );

        ObjectMapper mapper = new ObjectMapper();
        String templateJson = mapper.writeValueAsString(template);

        createRegistry(template);

        Response response = target(String.format("/registry/%s", tableName))
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, MockEnvironmentReader.TEST_REGISTRY_ADMIN_API_KEY)
                .get();

        SimpleResponse actual = response.readEntity(SimpleResponse.class);

        SimpleResponse expected = new SimpleResponse(templateJson, 200);
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void getEntity() throws Exception {
        String tableName = UUID.randomUUID().toString();
        EntityRegistryTemplate template = new EntityRegistryTemplate(tableName);
        createRegistry(template);

        Entry entry = sampleData.sampleEntry("entryId");
        Response response = insertEntryRequest(tableName, entry.jsonString());

        String entityPath = response.readEntity(SimpleResponse.class).getMessage();
        String entityId = entityPath.substring(entityPath.lastIndexOf("/") + 1);

        Response readEntityResponse = readEntity(tableName, entityId);
        assertThat(readEntityResponse.getStatus(), is(equalTo(Status.OK.getStatusCode())));

    }

    @Test
    public void putRegistrySchema() throws Exception {
        String registryName = UUID.randomUUID().toString();
        EntityRegistryTemplate template = new EntityRegistryTemplate(registryName);
        createRegistry(template);

        Entry entry = sampleData.sampleEntry("entryId");
        insertEntryRequest(registryName, entry.jsonString());

        String schemaAsJson = "Schema as Json";
        Response putRegistrySchemaResponse = putSchema(registryName, schemaAsJson);
        assertThat(putRegistrySchemaResponse.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        SimpleResponse actual = readSchema(registryName).readEntity(SimpleResponse.class);
        SimpleResponse expected = new SimpleResponse(schemaAsJson);
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void updateEntity() throws Exception {

        String registryName = UUID.randomUUID().toString();
        EntityRegistryTemplate template = new EntityRegistryTemplate(registryName);
        createRegistry(template);

        Entry entry = sampleData.sampleEntry("entityId");
        Response writeResponse = insertEntryRequest(registryName, entry.jsonString());
        SimpleResponse simpleResponse = writeResponse.readEntity(SimpleResponse.class);
        String path = simpleResponse.getMessage();
        String generatedId = path.substring(path.lastIndexOf("/") + 1);

        SampleData updatedSampleData = new SampleData();
        Entry updatedEntry = updatedSampleData.sampleEntry(generatedId);
        updatedEntry.root.remove("label");
        String updatedLabel = "An updated label";
        updatedEntry.root.put("label", updatedLabel);

        String updatedJson = updatedEntry.jsonString();
        Response response = updateEntryRequest(registryName, generatedId, updatedJson);
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
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, MockEnvironmentReader.TEST_API_ADMIN_API_KEY)
                .post(Entity.entity(jsonBody, MediaType.APPLICATION_JSON));

    }


    private Response createTable(String tableName) throws Exception {
        EntityRegistryTemplate createRequest = new EntityRegistryTemplate(tableName);
        return createRegistry(createRequest);
    }


    private Response createRegistry(EntityRegistryTemplate request) throws Exception {
        return target("/registry")
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, MockEnvironmentReader.TEST_API_ADMIN_API_KEY)
                .post(Entity.entity(request, MediaType.APPLICATION_JSON));
    }

    private Response readEntity(String registryName, String entityId) throws Exception {
        return target(String.format("/registry/%s/entity/%s", registryName, entityId))
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, MockEnvironmentReader.TEST_API_ADMIN_API_KEY)
                .get();
    }

    private Response putSchema(String registryName, String schemaAsJson) throws Exception {
        return target(String.format("/registry/%s/schema", registryName))
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, MockEnvironmentReader.TEST_REGISTRY_ADMIN_API_KEY)
                .put(Entity.entity(schemaAsJson, MediaType.APPLICATION_JSON));
    }

    private Response readSchema(String registryName) throws Exception {
        return target(String.format("/registry/%s/schema", registryName))
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, MockEnvironmentReader.TEST_REGISTRY_ADMIN_API_KEY)
                .get();
    }

    private Response updateEntryRequest(String registryName, String entityId, String jsonBody) {
        String path = String.format("/registry/%s/entity/%s", registryName, entityId);
        return target(path)
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, MockEnvironmentReader.TEST_API_ADMIN_API_KEY)
                .put(Entity.entity(jsonBody, MediaType.APPLICATION_JSON));

    }

    private Response deleteEntity(String registryName, String entityId) {

        return target(String.format("/registry/%s/entity/%s", registryName, entityId))
                .request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, MockEnvironmentReader.TEST_API_ADMIN_API_KEY)
                .delete();
    }
}
