package no.bibsys.web;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.s3.Headers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jsonldjava.core.JsonLdConsts;

import no.bibsys.JerseyConfig;
import no.bibsys.LocalDynamoDBHelper;
import no.bibsys.MockEnvironment;
import no.bibsys.aws.tools.Environment;
import no.bibsys.db.TableDriver;
import no.bibsys.service.ApiKey;
import no.bibsys.service.AuthenticationService;
import no.bibsys.testtemplates.SampleData;
import no.bibsys.utils.IoUtils;
import no.bibsys.utils.JsonUtils;
import no.bibsys.utils.ModelParser;
import no.bibsys.web.exception.validationexceptionmappers.ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeExceptionMapper;
import no.bibsys.web.exception.validationexceptionmappers.ValidationSchemaNotFoundExceptionMapper;
import no.bibsys.web.model.CustomMediaType;
import no.bibsys.web.model.EntityDto;
import no.bibsys.web.model.RegistryDto;
import no.bibsys.web.security.ApiKeyConstants;

public class DatabaseResourceTest extends JerseyTest {

    public static final String VALIDATION_FOLDER = "validation";
    public static final String INVALID_SHACL_VALIDATION_SCHEMA_JSON = "invalidDatatypeRangeShaclValidationSchema.json";
    public static final String VALID_SHACL_VALIDATION_SCHEMA_JSON = "validShaclValidationSchema.json";
    private static final String ENTITY_EXAMPLE_FILE = "src/test/resources/testdata/example_entity.%s";
    public static String REGISTRY_PATH = "/registry";
    private static String validValidationSchema;
    private final SampleData sampleData = new SampleData();
    private String apiAdminKey;
    private String registryAdminKey;

    @BeforeClass
    public static void init() throws IOException {
        System.setProperty("sqlite4java.library.path", "build/libs");
        validValidationSchema = IoUtils
            .resourceAsString(Paths.get(VALIDATION_FOLDER, VALID_SHACL_VALIDATION_SCHEMA_JSON));
    }

    @Override
    protected Application configure() {
        AmazonDynamoDB client = LocalDynamoDBHelper.getClient();
        Environment environmentReader = new MockEnvironment();

        TableDriver tableDriver = TableDriver.create(client);
        List<String> listTables = tableDriver.listTables();

        listTables.forEach(tableDriver::deleteTable);

        AuthenticationService authenticationService = new AuthenticationService(client, environmentReader);
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
    public void createRegistry_RegistryNotExistingUserNotAuthorized_StatusForbidden() throws Exception {
        String registryName = UUID.randomUUID().toString();

        Response response = createRegistry(registryName, "InvalidApiKey");
        assertThat(response.getStatus(), is(equalTo(Status.FORBIDDEN.getStatusCode())));
    }

    @Test
    public void createRegistry_RegistryNotExistingWrongUser_StatusForbidden() throws Exception {
        String registryName = UUID.randomUUID().toString();

        Response response = createRegistry(registryName, registryAdminKey);
        assertThat(response.getStatus(), is(equalTo(Status.FORBIDDEN.getStatusCode())));
    }

    @Test
    public void createRegistry_RegistryNotExistingUserAuthorized_StatusOK() throws Exception {
        String registryName = "TheRegistryName";
        RegistryDto expectedRegistry = sampleData.sampleRegistryDto(registryName);
        Response response = target(REGISTRY_PATH).request().accept(MediaType.APPLICATION_JSON)
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
            .buildPost(javax.ws.rs.client.Entity.entity(expectedRegistry, MediaType.APPLICATION_JSON)).invoke();

        RegistryDto actualRegistry = response.readEntity(RegistryDto.class);

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
        assertThat(actualRegistry.getId(), is(equalTo(expectedRegistry.getId())));
        assertThat(actualRegistry.getMetadata(), is(equalTo(expectedRegistry.getMetadata())));
    }

    @Test
    public void createRegistry_RegistryAlreadyExistsUserAuthorized_ReturnsStatusConflict() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);
        Response response = createRegistry(registryName, apiAdminKey);
        assertThat(response.getStatus(), is(equalTo(Status.CONFLICT.getStatusCode())));
    }

    @Test
    public void insertEntity_RegistryExistUserAuthorized_ReturnsStatusOK() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);
        putSchema(registryName, validValidationSchema);

        EntityDto expectedEntity = sampleData.sampleEntityDto();
        Response response = insertEntryRequest(registryName, expectedEntity, apiAdminKey);
        EntityDto actualEntity = response.readEntity(EntityDto.class);

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        Response readResponse = readEntity(registryName, actualEntity.getId(), MediaType.APPLICATION_JSON);
        EntityDto readEntity = readResponse.readEntity(EntityDto.class);

        assertThat(actualEntity.getId(), is(equalTo(expectedEntity.getId())));
        assertThat(actualEntity.isIsomorphic(expectedEntity), is(equalTo(true)));
        assertThat(readEntity, is(equalTo(actualEntity)));
    }

    @Test
    public void insertEntity_RegistryExistUserAuthorizedNoSchema_ReturnsBadRequest() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);

        EntityDto expectedEntity = sampleData.sampleEntityDto();
        Response response = insertEntryRequest(registryName, expectedEntity, apiAdminKey);
        String message = response.readEntity(String.class);

        assertThat(response.getStatus(), is(equalTo(Status.FORBIDDEN.getStatusCode())));
        assertThat(message, is(equalTo(ValidationSchemaNotFoundExceptionMapper.MESSAGE)));
    }

    @Test
    public void insertEntity_RegistryExistUserNotAuthorized_ReturnsStatusForbidden() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);

        EntityDto expectedEntity = sampleData.sampleEntityDto();
        Response response = insertEntryRequest(registryName, expectedEntity, "invalidKey");
        assertThat(response.getStatus(), is(equalTo(Status.FORBIDDEN.getStatusCode())));
    }

    @Test
    public void insertEntity_RegistryExistRegistryAdminUser_ReturnsStatusOk() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);
        putSchema(registryName, validValidationSchema);

        EntityDto expectedEntity = sampleData.sampleEntityDto();
        Response response = insertEntryRequest(registryName, expectedEntity, registryAdminKey);
        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
    }

    @Test
    public void deleteRegistry_RegistryExistsUserAuthorized_ReturnsStatusOk() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);
        putSchema(registryName, validValidationSchema);

        Response response = target("/registry/" + registryName).request()
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).delete();

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
        String entity = response.readEntity(String.class);
        String expected = String.format("Registry %s has been deleted", registryName);
        assertThat(entity, is(equalTo(expected)));
    }

    @Test
    public void deleteRegistry_RegistryExistsUserNotAuthorized_ReturnsStatusForbidden() throws Exception {
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
        Response response = target("/registry").request().header(ApiKeyConstants.API_KEY_PARAM_NAME, registryAdminKey)
            .post(javax.ws.rs.client.Entity.entity(registryDto, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus(), is(equalTo(Status.FORBIDDEN.getStatusCode())));
    }

    @Test
    public void getRegistryMetadata_RegistryExists_ReturnsStatusOk() throws Exception {

        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);

        createRegistry(registryDto, apiAdminKey);

        Response response = target(String.format("/registry/%s", registryName)).request()
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).accept(MediaType.TEXT_HTML).get();

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
    }

    @Test
    public void getEntity_RegistryExists_ReturnsStatusOk() throws Exception {
        String registryName = createRegistry();
        putSchema(registryName, validValidationSchema);

        Response response = createEntity(registryName);

        EntityDto readEntity = response.readEntity(EntityDto.class);

        Response readEntityResponse = readEntity(registryName, readEntity.getId(), MediaType.APPLICATION_JSON);
        assertThat(readEntityResponse.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        Assert.assertNotNull(readEntityResponse.getEntityTag());
        Assert.assertNotNull(readEntityResponse.getHeaderString(Headers.LAST_MODIFIED));
    }

    @Test
    public void getEntity_Twice_RegistryExists_ReturnsStatusNotModified() throws Exception {
        String registryName = createRegistry();
        putSchema(registryName, validValidationSchema);

        Response response = createEntity(registryName);

        EntityDto readEntity = response.readEntity(EntityDto.class);

        Response readEntityResponse = readEntity(registryName, readEntity.getId(), MediaType.APPLICATION_JSON);
        assertThat(readEntityResponse.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        Response readEntityResponseWithEntityTag = readEntityWithEntityTag(registryName, readEntity.getId(),
            readEntityResponse.getEntityTag());
        assertThat(readEntityResponseWithEntityTag.getStatus(), is(equalTo(Status.NOT_MODIFIED.getStatusCode())));
    }

    @Test
    public void getRegistryStatus_registryExists_returnsStatusCreated() throws Exception {
        String registryName = createRegistry();

        Response response = registryStatus(registryName);
        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
    }

    @Test
    public void putRegistrySchema_NonEmptyRegistry_ReturnsStatusMethodNotAllowed() throws Exception {
        String registryName = createRegistry();
        putSchema(registryName, validValidationSchema);

        EntityDto entity = sampleData.sampleEntityDto();
        insertEntryRequest(registryName, entity, apiAdminKey);

        String schemaAsJson = "Schema as Json";
        Response putRegistrySchemaResponse = putSchema(registryName, schemaAsJson);
        assertThat(putRegistrySchemaResponse.getStatus(), is(equalTo(Status.METHOD_NOT_ALLOWED.getStatusCode())));
    }

    @Test
    public void putRegistrySchema_RegistryExistsValidSchema_ReturnsStatusOK() throws Exception {
        String registryName = createRegistry();

        Response putRegistrySchemaResponse = putSchema(registryName, validValidationSchema);
        assertThat(putRegistrySchemaResponse.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        Response response = readSchema(registryName);
        RegistryDto registry = response.readEntity(RegistryDto.class);
        assertThat(validValidationSchema, is(equalTo(registry.getSchema())));
    }

    @Test
    public void putRegistrySchema_RegistryExistsInvalidSchema_ReturnsStatusBadRequest() throws Exception {
        String registryName = createRegistry();

        Response putRegistrySchemaResponse = putSchema(registryName, validValidationSchema);
        assertThat(putRegistrySchemaResponse.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        String invalidSchema = IoUtils
            .resourceAsString(Paths.get(VALIDATION_FOLDER, INVALID_SHACL_VALIDATION_SCHEMA_JSON));
        Response invalidSchemaResponse = putSchema(registryName, invalidSchema);
        String message = invalidSchemaResponse.readEntity(String.class);
        assertThat(message, is(equalTo(ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeExceptionMapper.MESSAGE)));
    }

    @Test
    public void updateEntity_EntityExists_ReturnsUpdatedEntity() throws Exception {

        String registryName = createRegistry();
        putSchema(registryName, validValidationSchema);

        Response writeResponse = createEntity(registryName);
        EntityDto writeEntity = writeResponse.readEntity(EntityDto.class);
        String entityId = writeEntity.getId();

        String newLabel = "An updated label";

        ObjectMapper mapper = new ObjectMapper();
        EntityDto updatedEntity = updateEntityLabel(newLabel, mapper);

        Response response = updateEntityRequest(registryName, updatedEntity);
        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        Response readEntityResponse = readEntity(registryName, entityId, MediaType.APPLICATION_JSON);

        EntityDto readEntity = readEntityResponse.readEntity(EntityDto.class);
        String actual = mapper.readValue(readEntity.getBody(), ObjectNode.class).get("label").asText();
        assertThat(actual, is(equalTo(newLabel)));
    }

    private EntityDto updateEntityLabel(String newLabel, ObjectMapper mapper) throws IOException {

        SampleData updatedSampleData = new SampleData();
        EntityDto updatedEntity = updatedSampleData.sampleEntityDto();
        ObjectNode body = mapper.readValue(updatedEntity.getBody(), ObjectNode.class);
        body.remove("label");
        body.put("label", newLabel);
        updatedEntity.setBody(mapper.writeValueAsString(body));
        return updatedEntity;
    }

    @Test
    public void uploadArrayOfThreeEntities_RegistryExists_RegistryContainsThreeEntities() throws Exception {

        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        createRegistry(registryDto, apiAdminKey);

        putSchema(registryDto.getId(), validValidationSchema);
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
    public void replaceApiKey_RegistryNotExisting_ReturnsStatusNotFound() throws Exception {
        String registryName = UUID.randomUUID().toString();
        String oldApiKey = UUID.randomUUID().toString(); // random non-existing apikey

        Response newApiKeyResponse = replaceApiKey(registryName, oldApiKey);

        assertThat(newApiKeyResponse.getStatus(), is(equalTo(Status.NOT_FOUND.getStatusCode())));
    }

    @Test
    public void replaceApiKey_RegistryExistingWrongApiKey_ReturnsStatusBadRequest() throws Exception {
        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        createRegistry(registryDto, apiAdminKey);
        String oldApiKey = UUID.randomUUID().toString(); // random non-existing apikey

        Response newApiKeyResponse = replaceApiKey(registryName, oldApiKey);

        assertThat(newApiKeyResponse.getStatus(), is(equalTo(Status.BAD_REQUEST.getStatusCode())));
    }

    @Test
    public void uploadArrayOfThreeEntities_RegistryNotExisting_ReturnsStatusNotFound() throws Exception {

        String registryName = UUID.randomUUID().toString();
        List<EntityDto> sampleEntities = createSampleEntities();

        Response response = uploadEntities(registryName, sampleEntities);
        assertThat(response.getStatus(), is(equalTo(Status.NOT_FOUND.getStatusCode())));
    }

    @Test
    public void getEntity_textHtml_entityAsHtml() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);
        putSchema(registryName, validValidationSchema);
        EntityDto entity = createEntity(registryName).readEntity(EntityDto.class);

        Response entityAsHtml = readEntity(registryName, entity.getId(), MediaType.TEXT_HTML);
        String html = entityAsHtml.readEntity(String.class);

        assertThat(html.toLowerCase(), containsString("html"));
        JsonNode body = JsonUtils.newJsonParser().readTree(entity.getBody());
        Iterable<String> bodyIter = body::fieldNames;
        List<String> bodyFields = StreamSupport.stream(bodyIter.spliterator(), false).collect(Collectors.toList());

        bodyFields.stream().filter(field -> !field.toLowerCase().equals(JsonLdConsts.CONTEXT)).forEach(
            field -> assertThat(html.toLowerCase(),
                containsString("data-automation-id=\"" + field.toLowerCase() + "\"")));
    }

    @Test
    public void getEntity_applicationJson_entityAsJson() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);
        putSchema(registryName, validValidationSchema);

        EntityDto entity = createEntity(registryName).readEntity(EntityDto.class);

        Response entityAsJson = getEntityAsJson(registryName, entity.getId());
        String json = entityAsJson.readEntity(String.class);

        ObjectMapper mapper = new ObjectMapper();
        EntityDto readEntity = mapper.readValue(json, EntityDto.class);

        assertThat(readEntity.getBody(), containsString(entity.getBody()));
    }

    @Test
    public void getEntity_applicationRdf_entityAsRdf() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);
        putSchema(registryName, validValidationSchema);
        EntityDto entity = createEntity(registryName).readEntity(EntityDto.class);

        Response entityAsRdf = readEntity(registryName, entity.getId(), CustomMediaType.APPLICATION_RDF);
        String rdf = entityAsRdf.readEntity(String.class);

        Lang lang = Lang.RDFXML;
        ModelParser parser = new ModelParser();
        Model actualModel = parser.parseModel(new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8)), lang);
        String testFile = String.format(ENTITY_EXAMPLE_FILE, lang.getLabel().replaceAll("/", ""));
        Model expectedModel = parser.parseModel(new FileInputStream(new File(testFile)), lang);

        assertThat(actualModel.isIsomorphicWith(expectedModel), is(true));
    }

    @Test
    public void getEntity_applicationNtriples_entityAsNtriples() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);
        putSchema(registryName, validValidationSchema);
        EntityDto entity = createEntity(registryName).readEntity(EntityDto.class);

        Response entityAsTriples = readEntity(registryName, entity.getId(), CustomMediaType.APPLICATION_N_TRIPLES);
        String triples = entityAsTriples.readEntity(String.class);

        Lang lang = Lang.NTRIPLES;
        ModelParser parser = new ModelParser();
        Model actualModel = parser.parseModel(new ByteArrayInputStream(triples.getBytes(StandardCharsets.UTF_8)), lang);
        String testFile = String.format(ENTITY_EXAMPLE_FILE, lang.getLabel().replaceAll("/", ""));
        Model expectedModel = parser.parseModel(new FileInputStream(new File(testFile)), lang);

        assertThat(actualModel.isIsomorphicWith(expectedModel), is(true));
    }

    @Test
    public void getEntity_applicationTurtle_entityAsTurtle() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);
        putSchema(registryName, validValidationSchema);
        EntityDto entity = createEntity(registryName).readEntity(EntityDto.class);

        Response entityAsTurtle = readEntity(registryName, entity.getId(), CustomMediaType.APPLICATION_TURTLE);
        String turtle = entityAsTurtle.readEntity(String.class);

        Lang lang = Lang.TURTLE;
        ModelParser parser = new ModelParser();
        Model actualModel = parser.parseModel(new ByteArrayInputStream(turtle.getBytes(StandardCharsets.UTF_8)), lang);
        String testFile = String.format(ENTITY_EXAMPLE_FILE, lang.getLabel().replaceAll("/", "").toUpperCase());
        Model expectedModel = parser.parseModel(new FileInputStream(new File(testFile)), lang);

        assertThat(actualModel.isIsomorphicWith(expectedModel), is(true));
    }
    
    @Test
    public void getRegistryMetadata_textHtml_registryAsHtml() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);

        Response entityAsHtml = getRegistry(registryName, MediaType.TEXT_HTML);
        String html = entityAsHtml.readEntity(String.class);

        assertThat(html, containsString("html"));
        assertThat(html, containsString("<title>Registry name value</title>"));
        assertThat(html, containsString("data-automation-id=\"Registry_name\""));
        assertThat(html, containsString("data-automation-id=\"Publisher\""));
    }

    private Response getRegistry(String registryName, String mediaType) throws Exception {
        return target(String.format("/registry/%s", registryName)).request()
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).accept(mediaType).get();
    }

    private Response readEntity(String registryName, String entityId, String mediaType) throws Exception {
        return target(String.format("/registry/%s/entity/%s", registryName, entityId)).request()
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).accept(mediaType).get();
    }

    private Response putSchema(String registryName, String schemaAsJson) throws Exception {
        return target(String.format("/registry/%s/schema", registryName)).request()
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, registryAdminKey)
            .put(javax.ws.rs.client.Entity.entity(schemaAsJson, MediaType.APPLICATION_JSON));
    }

    private Response createRegistry(String registryName, String apiKey) throws Exception {
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        return createRegistry(registryDto, apiKey);
    }

    private Response createRegistry(RegistryDto registryDto, String apiKey) {
        Response response = target("/registry").request().accept(MediaType.APPLICATION_JSON)
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiKey)
            .post(javax.ws.rs.client.Entity.entity(registryDto, MediaType.APPLICATION_JSON));
        return response;
    }

    private String createRegistry() throws Exception {
        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        createRegistry(registryDto, apiAdminKey);
        return registryName;
    }

    private Response insertEntryRequest(String registryName, EntityDto entityDto, String apiKey) {
        String path = String.format("/registry/%s/entity", registryName);
        return target(path).request().header(ApiKeyConstants.API_KEY_PARAM_NAME, apiKey)
            .post(javax.ws.rs.client.Entity.entity(entityDto, MediaType.APPLICATION_JSON));
    }

    private Response createEntity(String registryName) throws IOException {
        EntityDto entity = sampleData.sampleEntityDto();
        Response writeResponse = insertEntryRequest(registryName, entity, apiAdminKey);
        return writeResponse;
    }

    private Response readEntityWithEntityTag(String registryName, String entityId, EntityTag entityTag) {
        return target(String.format("/registry/%s/entity/%s", registryName, entityId)).request()
            .header("If-None-Match", "\"" + entityTag.getValue() + "\"")
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).get();
    }

    private Response registryStatus(String registryName) {
        Response response = target(String.format("/registry/%s/status", registryName)).request().get();
        return response;
    }

    private Response updateEntityRequest(String registryName, EntityDto entityDto) {
        String path = String.format("/registry/%s/entity/%s", registryName, entityDto.getId());
        return target(path).request().header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
            .put(javax.ws.rs.client.Entity.entity(entityDto, MediaType.APPLICATION_JSON));
    }

    private List<EntityDto> createSampleEntities() throws IOException {
        List<EntityDto> sampleEntities = new CopyOnWriteArrayList<EntityDto>();
        sampleEntities.add(createSampleEntity(UUID.randomUUID().toString()));
        sampleEntities.add(createSampleEntity(UUID.randomUUID().toString()));
        sampleEntities.add(createSampleEntity(UUID.randomUUID().toString()));

        return sampleEntities;
    }

    private EntityDto createSampleEntity(String identifier) throws IOException {
        EntityDto sampleEntityDto = sampleData.sampleEntityDto();
        sampleEntityDto.setId(identifier);
        return sampleEntityDto;
    }

    private Response uploadEntities(String registryName, List<EntityDto> sampleEntities) {

        String path = String.format("/registry/%s/upload", registryName);
        return target(path).request(MediaType.APPLICATION_JSON).header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
            .post(javax.ws.rs.client.Entity.entity(sampleEntities, MediaType.APPLICATION_JSON));
    }

    private Response getEntityAsJson(String registryName, String id) throws Exception {
        return target(String.format("/registry/%s/entity/%s", registryName, id)).request()
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).accept(MediaType.APPLICATION_JSON).get();
    }

    private Response readSchema(String registryName) throws Exception {
        return target(String.format("/registry/%s/schema", registryName)).request()
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).get();
    }

    private Response replaceApiKey(String registryName, String oldApiKey) {
        String path = String.format("/registry/%s/apikey", registryName);
        return target(path).request().header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
            .put(javax.ws.rs.client.Entity.entity(oldApiKey, MediaType.APPLICATION_JSON));
    }
}
