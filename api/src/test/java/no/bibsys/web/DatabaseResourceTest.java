package no.bibsys.web;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Test;

import no.bibsys.JerseyConfig;
import no.bibsys.LocalDynamoDBHelper;
import no.bibsys.db.DatabaseManager;
import no.bibsys.testtemplates.SampleData;
import no.bibsys.testtemplates.SampleData.Entry;
import no.bibsys.web.model.EditRegistryRequest;
import no.bibsys.web.model.PathResponse;
import no.bibsys.web.model.SimpleResponse;


public class DatabaseResourceTest extends JerseyTest {

    private final static String TABLE_NAME = "DatabaseControllerAPITest";
    private final SampleData sampleData = new SampleData();

    @Override
    protected Application configure() {
        return new JerseyConfig(new DatabaseManager(LocalDynamoDBHelper.getTableDriver()));
    }

    @BeforeClass
    public static void init() {
        System.setProperty("sqlite4java.library.path", "build/libs");
    }
    
    @Test
    public void returnDefaultMessage() throws Exception {

        SimpleResponse expected = new SimpleResponse("Hello");

        SimpleResponse actual = target("/hello").request().get(SimpleResponse.class);

        assertThat(actual, is(equalTo(expected)));
    }


    @Test
    public void sendSuccessWhenCreatingNonExistingTable() throws Exception {
        SimpleResponse expected = new SimpleResponse(String.format("A registry with name %s has been created", TABLE_NAME));

        Response response = createTable(TABLE_NAME);

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        SimpleResponse actual = response.readEntity(SimpleResponse.class);
        assertThat(actual, is(equalTo(expected)));
    }


    @Test
    public void sendConflictWhenCreatingExistingTable() throws Exception {
        String tableName = "createTableAPITest";
        createTable(tableName);
        Response response = createTable(tableName);
        assertThat(response.getStatus(), is(equalTo(Status.CONFLICT.getStatusCode())));
    }

    @Test
    public void insertEntryInTable() throws Exception {
        createTable(TABLE_NAME);
        Entry entry = sampleData.sampleEntry("entryId");
        PathResponse expected =
                new PathResponse(String.format("/registry/%s/%s", TABLE_NAME, entry.id));

        Response response = insertEntryRequest(TABLE_NAME, entry.jsonString());
        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        PathResponse actual = response.readEntity(PathResponse.class);
        assertThat(actual, is(equalTo(expected)));
    }


    @Test
    public void throwExceptionOnDuplicateEntries() throws Exception {
        createTable(TABLE_NAME);
        Entry entry = sampleData.sampleEntry("entryId");


        Response response1 = insertEntryRequest(TABLE_NAME, entry.jsonString());
        assertThat(response1.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        Response response2 = insertEntryRequest(TABLE_NAME, entry.jsonString());
        assertThat(response2.getStatus(), is(equalTo(Status.CONFLICT.getStatusCode())));

         SimpleResponse expected =
         new SimpleResponse(String.format("Item already exists"));
        
         SimpleResponse actual = response2.readEntity(SimpleResponse.class);
        
         assertThat(actual, is(equalTo(expected)));


    }


    @Test
    public void deleteAnExistingRegistry() throws Exception {
        createTable(TABLE_NAME);

        Response response = target("/registry/" + TABLE_NAME).request().delete();

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
        SimpleResponse actual = response.readEntity(SimpleResponse.class);
        SimpleResponse expected =
                new SimpleResponse(String.format("Registry %s has been deleted", TABLE_NAME));
        assertThat(actual, is(equalTo(expected)));

    }


    @Test
    public void returnErrorWhenDeletingNonExistingRegistry() throws Exception {

        Response response = target("/registry/" + TABLE_NAME).request().delete();
        assertThat(response.getStatus(), is(equalTo(Status.NOT_FOUND.getStatusCode())));

         SimpleResponse actual = response.readEntity(SimpleResponse.class);
         SimpleResponse expected =
         new SimpleResponse("Table does not exist");
         assertThat(actual, is(equalTo(expected)));

    }


    @Test
    public void emptyAnExistingTable() throws Exception {
        createTable(TABLE_NAME);
        String entry = sampleData.sampleEntry("entryId").jsonString();

        insertEntryRequest(TABLE_NAME, entry);

        Response response = target(String.format("/registry/%s/empty", TABLE_NAME)).request()
                .delete();

        SimpleResponse actual = response.readEntity(SimpleResponse.class);
        SimpleResponse expected = new SimpleResponse(String.format("Registry %s has been emptied", TABLE_NAME));
        assertThat(actual, is(equalTo(expected)));
    }

    @Test 
    public void getListOfRegistries() throws Exception {
        createTable(TABLE_NAME);
        
        Response response = target("/registry")
                .request()
                .get();

        SimpleResponse actual = response.readEntity(SimpleResponse.class);
        SimpleResponse expected = new SimpleResponse(String.format("[\"%s\"]", TABLE_NAME), 200);
        assertThat(actual, is(equalTo(expected)));
    }
    

    private Response insertEntryRequest(String registryName, String jsonBody) {
        String path = String.format("/registry/%s/", registryName);
        return target(path).request().post(Entity.entity(jsonBody, MediaType.APPLICATION_JSON));

    }


    private Response createTable(String tableName) throws Exception {
        EditRegistryRequest createRequest = new EditRegistryRequest(tableName);
        return createTableRequest(createRequest);
    }


    private Response createTableRequest(EditRegistryRequest request) throws Exception {
        return target("/registry")
                .request()
                .post(Entity.entity(request, MediaType.APPLICATION_JSON));
    }


}
