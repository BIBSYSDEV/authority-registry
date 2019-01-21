
package no.bibsys.web;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.s3.Headers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import no.bibsys.JerseyConfig;
import no.bibsys.LocalDynamoDBHelper;
import no.bibsys.MockEnvironment;
import no.bibsys.aws.tools.Environment;
import no.bibsys.db.TableDriver;
import no.bibsys.service.ApiKey;
import no.bibsys.service.AuthenticationService;
import no.bibsys.testtemplates.SampleData;
import no.bibsys.web.model.EntityDto;
import no.bibsys.web.model.MediaTypeRdf;
import no.bibsys.web.model.RegistryDto;
import no.bibsys.web.security.ApiKeyConstants;


public class DatabaseResourceTest extends JerseyTest {

    public static String REGISTRY_PATH = "/registry";
    private final SampleData sampleData = new SampleData();
    private String apiAdminKey;
    private String registryAdminKey;

    @BeforeClass
    public static void init() {
        System.setProperty("sqlite4java.library.path", "build/libs");
    }

    @Override
    protected Application configure() {
        AmazonDynamoDB client = LocalDynamoDBHelper.getClient();
        Environment environmentReader = new MockEnvironment();

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

    @Test
    public void ping_ReturnsStatusCodeOK() throws Exception {

        Response response = target("/ping").request().get();

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
    }


    @Test
    public void createRegistry_RegistryNotExistingUserNotAuthorized_StatusForbidden()
        throws Exception {
        String registryName = UUID.randomUUID().toString();

        Response response = createRegistry(registryName, "InvalidApiKey");
        assertThat(response.getStatus(), is(equalTo(Status.FORBIDDEN.getStatusCode())));

    }

    @Test
    public void createRegistry_RegistryNotExistingWrongUser_StatusForbidden()
        throws Exception {
        String registryName = UUID.randomUUID().toString();

        Response response = createRegistry(registryName, registryAdminKey);
        assertThat(response.getStatus(), is(equalTo(Status.FORBIDDEN.getStatusCode())));

    }


    @Test
    public void createRegistry_RegistryNotExistingUserAuthorized_StatusOK() throws Exception {
        String registryName = "TheRegistryName";

//        new CreatedRegistryDto(String.format("A registry with name=%s is being created", registryName));
        RegistryDto expectedRegistry = sampleData.sampleRegistryDto(registryName);
        Response response = target(REGISTRY_PATH).request()
            .accept(MediaType.APPLICATION_JSON)
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
            .buildPost(
                javax.ws.rs.client.Entity.entity(expectedRegistry, MediaType.APPLICATION_JSON))
            .invoke();

        RegistryDto actualRegistry = response.readEntity(RegistryDto.class);

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
        assertThat(actualRegistry.getId(), is(equalTo(expectedRegistry.getId())));
        assertThat(actualRegistry.getMetadata(), is(equalTo(expectedRegistry.getMetadata())));

    }


    @Test
    public void createRegistry_RegistryAlreadyExistsUserAuthorized_ReturnsStatusConflict()
        throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);
        Response response = createRegistry(registryName, apiAdminKey);
        assertThat(response.getStatus(), is(equalTo(Status.CONFLICT.getStatusCode())));
    }

    @Test
    public void insertEntity_RegistryExistUserAuthorized_ReturnsStatusOK() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);

        EntityDto expectedEntity = sampleData.sampleEntityDto();
        Response response = insertEntryRequest(registryName, expectedEntity, apiAdminKey);
        EntityDto actualEntity = response.readEntity(EntityDto.class);

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        Response readResponse = readEntity(registryName, actualEntity.getId(),
            MediaType.APPLICATION_JSON);
        EntityDto readEntity = readResponse.readEntity(EntityDto.class);

        assertThat(actualEntity.getId(), is(equalTo(expectedEntity.getId())));
        assertThat(actualEntity.getBody(), is(equalTo(expectedEntity.getBody())));
        assertThat(readEntity, is(equalTo(actualEntity)));


    }


    @Test
    public void insertEntity_RegistryExistUserNotAuthorized_ReturnsStatusForbidden()
        throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);

        EntityDto expectedEntity = sampleData.sampleEntityDto();
        Response response = insertEntryRequest(registryName, expectedEntity, "invalidKey");
        assertThat(response.getStatus(), is(equalTo(Status.FORBIDDEN.getStatusCode())));
    }


    @Test
    public void insertEntity_RegistryExistRegistryAdminUser_ReturnsStatusOK()
        throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);

        EntityDto expectedEntity = sampleData.sampleEntityDto();
        Response response = insertEntryRequest(registryName, expectedEntity, registryAdminKey);
        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
    }


    @Test
    public void deleteRegistry_RegistryExistsUserAuthorized_ReturnsStatusOK() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);

        Response response = target("/registry/" + registryName).request()
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).delete();

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
        String entity = response.readEntity(String.class);
        String expected = String.format("Registry %s has been deleted", registryName);
        assertThat(entity, is(equalTo(expected)));

    }


    @Test
    public void deleteRegistry_RegistryExistsUserNotAuthorized_ReturnsStatusForbidden()
        throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);

        Response response = target("/registry/" + registryName).request()
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, "invalidAPIKEY").delete();

        assertThat(response.getStatus(), is(equalTo(Status.FORBIDDEN.getStatusCode())));
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
    public void callEndpoint_WrongRole_ReturnsStatusForbidden() throws Exception {
        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        Response response = target("/registry").request()
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, registryAdminKey)
            .post(javax.ws.rs.client.Entity.entity(registryDto, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus(), is(equalTo(Status.FORBIDDEN.getStatusCode())));

    }

    @Test
    public void getRegistryMetadata_RegistryExists_ReturnsStatusOk() throws Exception {

        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);

        createRegistry(registryDto, apiAdminKey);

        Response response = target(String.format("/registry/%s", registryName)).request()
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).accept(MediaType.TEXT_HTML)
            .get();

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
    }

    @Test
    public void getEntity_RegistryExists_ReturnsStatusOK() throws Exception {
        String registryName = createRegistry();

        Response response = createEntity(registryName);

        EntityDto readEntity = response.readEntity(EntityDto.class);

        Response readEntityResponse = readEntity(registryName, readEntity.getId(),
            MediaType.APPLICATION_JSON);
        assertThat(readEntityResponse.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        Assert.assertNotNull(readEntityResponse.getEntityTag());
        Assert.assertNotNull(readEntityResponse.getHeaderString(Headers.LAST_MODIFIED));

    }

    @Test
    public void getEntity_Twice_RegistryExists_ReturnsStatusNotModified() throws Exception {
        String registryName = createRegistry();

        Response response = createEntity(registryName);

        EntityDto readEntity = response.readEntity(EntityDto.class);

        Response readEntityResponse = readEntity(registryName, readEntity.getId(),
            MediaType.APPLICATION_JSON);
        assertThat(readEntityResponse.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        Response readEntityResponseWithEntityTag = readEntityWithEntityTag(registryName,
            readEntity.getId(), readEntityResponse.getEntityTag());
        assertThat(readEntityResponseWithEntityTag.getStatus(),
            is(equalTo(Status.NOT_MODIFIED.getStatusCode())));
    }

    @Test
    public void getRegistryStatus_registryExists_returnsStatusCreated() throws Exception {
        String registryName = createRegistry();

        Response response = registryStatus(registryName);
        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
    }

    @Test
    public void putRegistrySchema_NonEmptyRegistry_ReturnsStatusMETHOD_NOT_ALLOWED()
        throws Exception {
        String registryName = createRegistry();

        EntityDto entity = sampleData.sampleEntityDto();
        insertEntryRequest(registryName, entity, apiAdminKey);

        String schemaAsJson = "Schema as Json";
        Response putRegistrySchemaResponse = putSchema(registryName, schemaAsJson);
        assertThat(putRegistrySchemaResponse.getStatus(),
            is(equalTo(Status.METHOD_NOT_ALLOWED.getStatusCode())));

    }

    @Test
    public void putRegistrySchema_RegistryExists_ReturnsStatusOK() throws Exception {
        String registryName = createRegistry();

        String schemaAsJson = "Schema as Json";
        Response putRegistrySchemaResponse = putSchema(registryName, schemaAsJson);
        assertThat(putRegistrySchemaResponse.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        Response response = readSchema(registryName);
        RegistryDto registry = response.readEntity(RegistryDto.class);
        assertThat(schemaAsJson, is(equalTo(registry.getSchema())));
    }


    @Test
    public void updateEntity_EntityExists_ReturnsUpdatedEntity() throws Exception {

        String registryName = createRegistry();

        Response writeResponse = createEntity(registryName);
        EntityDto writeEntity = writeResponse.readEntity(EntityDto.class);

        SampleData updatedSampleData = new SampleData();

        EntityDto updatedEntity = updatedSampleData.sampleEntityDto();

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode body = mapper.readValue(updatedEntity.getBody(), ObjectNode.class);

        body.remove("label");
        String updatedLabel = "An updated label";
        body.put("label", updatedLabel);

        updatedEntity.setBody(mapper.writeValueAsString(body));

        Response response = updateEntityRequest(registryName,
            updatedEntity);
        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        Response readEntityResponse = readEntity(registryName, writeEntity.getId(),
            MediaType.APPLICATION_JSON);

        EntityDto readEntity = readEntityResponse.readEntity(EntityDto.class);
        String actual = mapper.readValue(readEntity.getBody(), ObjectNode.class).get("label")
            .asText();
        assertThat(actual, is(equalTo(updatedLabel)));

    }

    private Response createEntity(String registryName) throws JsonProcessingException {
        EntityDto entity = sampleData.sampleEntityDto();
        Response writeResponse = insertEntryRequest(registryName, entity, apiAdminKey);
        return writeResponse;
    }

    @Test
    public void uploadArrayOfThreeEntities_RegistryExists_RegistryContainsThreeEntities()
        throws Exception {

        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        createRegistry(registryDto, apiAdminKey);

        List<EntityDto> sampleEntities = createSampleEntities();

        Response response = uploadEntities(registryName, sampleEntities);
        List<EntityDto> readEntityList = response.readEntity(new GenericType<List<EntityDto>>() {
        });
        AtomicInteger numberOfEntities = new AtomicInteger(0);

        readEntityList.forEach(entity -> {
            try {
                readEntity(registryName, entity.getId(), MediaType.APPLICATION_JSON);
                numberOfEntities.set(numberOfEntities.incrementAndGet());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        assertThat(numberOfEntities.get(), is(equalTo(3)));
    }

    @Test
    public void replaceApiKey_RegistryExists_ReturnsNewApiKey() throws Exception {
        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        Response createRegistryResponse = createRegistry(registryDto, apiAdminKey);
        RegistryDto newRegistry = createRegistryResponse.readEntity(RegistryDto.class);
        String oldApiKey = newRegistry.getApiKey();

        Response newApiKeyResponse = replaceApiKey(registryName, oldApiKey);
        String newApiKey = newApiKeyResponse.readEntity(String.class);

        assertThat(newApiKey, is(not(equalTo(oldApiKey))));
    }

    @Test
    public void replaceApiKey_RegistryNotExisting_ReturnsStatusNOT_FOUND() throws Exception {
        String registryName = UUID.randomUUID().toString();
        String oldApiKey = UUID.randomUUID().toString(); // random non-existing apikey

        Response newApiKeyResponse = replaceApiKey(registryName, oldApiKey);

        assertThat(newApiKeyResponse.getStatus(), is(equalTo(Status.NOT_FOUND.getStatusCode())));
    }

    @Test
    public void replaceApiKey_RegistryExistingWrongApiKey_ReturnsStatusBAD_REQUEST()
        throws Exception {
        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        createRegistry(registryDto, apiAdminKey);
        String oldApiKey = UUID.randomUUID().toString(); // random non-existing apikey

        Response newApiKeyResponse = replaceApiKey(registryName, oldApiKey);

        assertThat(newApiKeyResponse.getStatus(), is(equalTo(Status.BAD_REQUEST.getStatusCode())));
    }

    private String createRegistry() throws Exception {
        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        createRegistry(registryDto, apiAdminKey);
        return registryName;
    }

    private Response replaceApiKey(String registryName, String oldApiKey) {
        String path = String.format("/registry/%s/apikey", registryName);
        return target(path).request().header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
            .put(javax.ws.rs.client.Entity.entity(oldApiKey, MediaType.APPLICATION_JSON));
    }


    @Test
    public void uploadArrayOfThreeEntities_RegistryNotExisting_ReturnsStatusNotFound()
        throws Exception {

        String registryName = UUID.randomUUID().toString();
        List<EntityDto> sampleEntities = createSampleEntities();

        Response response = uploadEntities(registryName, sampleEntities);
        assertThat(response.getStatus(), is(equalTo(Status.NOT_FOUND.getStatusCode())));
    }

    @Test
    public void getEntity_textHtml_entityAsHtml() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);
        EntityDto entity = createEntity(registryName).readEntity(EntityDto.class);

        Response entityAsHtml = readEntity(registryName, entity.getId(), MediaType.TEXT_HTML);
        String html = entityAsHtml.readEntity(String.class);
        
        assertThat(html.toLowerCase(), containsString("html"));
        assertThat(html.toLowerCase(), containsString("data-automation-id=\"alternativelabel\""));
        assertThat(html.toLowerCase(), containsString("data-automation-id=\"inscheme\""));
        assertThat(html.toLowerCase(), containsString("data-automation-id=\"narrower\""));
        assertThat(html.toLowerCase(), containsString("data-automation-id=\"preferredlabel\""));
    }

    @Test
    public void getEntity_applicationRdf_entityAsRdf() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);
        EntityDto entity = createEntity(registryName).readEntity(EntityDto.class);
        
        System.out.println(entity.getBody());
        
        Response entityAsRdf = readEntity(registryName, entity.getId(), MediaTypeRdf.APPLICATION_RDF);
        String rdf = entityAsRdf.readEntity(String.class);
        
        System.out.println(rdf);
    }
    
    @Test
    public void getEntity_applicationJson_entityAsJson() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);
        EntityDto entity = createEntity(registryName).readEntity(EntityDto.class);

        Response entityAsJson = getEntityAsJson(registryName, entity.getId());
        String json = entityAsJson.readEntity(String.class);

        ObjectMapper mapper = new ObjectMapper();
        EntityDto readEntity = mapper.readValue(json, EntityDto.class);

        assertThat(readEntity.getBody(), containsString(entity.getBody()));
    }

    @Test
    public void getRegistryMetadata_textHtml_registryAsHtml() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);

        Response entityAsHtml = getRegistryAsHtml(registryName);
        String html = entityAsHtml.readEntity(String.class);

        assertThat(html, containsString("html"));
        assertThat(html, containsString("<title>Registry name value</title>"));
        assertThat(html, containsString("data-automation-id=\"Registry_name\""));
        assertThat(html, containsString("data-automation-id=\"Publisher\""));
    }

    private List<EntityDto> createSampleEntities() throws JsonProcessingException {
        List<EntityDto> sampleEntities = new CopyOnWriteArrayList<EntityDto>();
        sampleEntities.add(createSampleEntity(UUID.randomUUID().toString()));
        sampleEntities.add(createSampleEntity(UUID.randomUUID().toString()));
        sampleEntities.add(createSampleEntity(UUID.randomUUID().toString()));

        return sampleEntities;
    }

    private EntityDto createSampleEntity(String identifier) throws JsonProcessingException {
        EntityDto sampleEntityDto = sampleData.sampleEntityDto();
        sampleEntityDto.setId(identifier);
        return sampleEntityDto;
    }

    private Response getEntityAsHtml(String registryName, String id) throws Exception {
        return target(String.format("/registry/%s/entity/%s", registryName, id)).request()
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).accept(MediaType.TEXT_HTML)
            .get();
    }

    private Response getEntityAsJson(String registryName, String id) throws Exception {
        return target(String.format("/registry/%s/entity/%s", registryName, id)).request()
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
            .accept(MediaType.APPLICATION_JSON).get();
    }

    private Response uploadEntities(String registryName, List<EntityDto> sampleEntities) {

        String path = String.format("/registry/%s/upload", registryName);
        return target(path).request(MediaType.APPLICATION_JSON)
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
            .post(javax.ws.rs.client.Entity.entity(sampleEntities, MediaType.APPLICATION_JSON));
    }

    private Response insertEntryRequest(String registryName, EntityDto entityDto, String apiKey) {
        String path = String.format("/registry/%s/entity", registryName);
        return target(path).request().header(ApiKeyConstants.API_KEY_PARAM_NAME, apiKey)
            .post(javax.ws.rs.client.Entity.entity(entityDto, MediaType.APPLICATION_JSON));

    }


    private Response createRegistry(String registryName, String apiKey) throws Exception {
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        return createRegistry(registryDto, apiKey);
    }


    private Response createRegistry(RegistryDto registryDto, String apiKey) {
        Response response = target("/registry").request()
            .accept(MediaType.APPLICATION_JSON)
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiKey)
            .post(javax.ws.rs.client.Entity.entity(registryDto, MediaType.APPLICATION_JSON));
        return response;
    }

    private Response getRegistryAsHtml(String registryName) throws Exception {
        return target(String.format("/registry/%s", registryName)).request()
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).accept(MediaType.TEXT_HTML)
            .get();
    }

    private Response registryStatus(String registryName) {
        Response response =
            target(String.format("/registry/%s/status", registryName)).request().get();
        return response;
    }

    private Response readEntity(String registryName, String entityId, String mediaType)
        throws Exception {
        return target(String.format("/registry/%s/entity/%s", registryName, entityId)).request()
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).accept(mediaType).get();
    }

    private Response readEntityWithEntityTag(String registryName, String entityId,
        EntityTag entityTag) {
        return target(String.format("/registry/%s/entity/%s", registryName, entityId)).request()
            .header("If-None-Match", "\"" + entityTag.getValue() + "\"")
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

    private Response updateEntityRequest(String registryName, EntityDto entityDto) {
        String path = String.format("/registry/%s/entity/%s", registryName, entityDto.getId());
        return target(path).request().header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
            .put(javax.ws.rs.client.Entity.entity(entityDto, MediaType.APPLICATION_JSON));

    }
}
