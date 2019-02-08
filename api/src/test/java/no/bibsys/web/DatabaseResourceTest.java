package no.bibsys.web;


import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

import no.bibsys.JerseyConfig;
import no.bibsys.LocalDynamoDBHelper;
import no.bibsys.MockEnvironment;
import no.bibsys.aws.tools.Environment;
import no.bibsys.db.TableDriver;
import no.bibsys.service.ApiKey;
import no.bibsys.service.AuthenticationService;
import no.bibsys.testtemplates.SampleData;
import no.bibsys.utils.IoUtils;
import no.bibsys.web.model.EntityDto;
import no.bibsys.web.model.RegistryDto;
import no.bibsys.web.security.ApiKeyConstants;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

public class DatabaseResourceTest extends JerseyTest {

    protected static final String MARC_SUBFIELD = "<marc:subfield code=\"a\">norsklabel</marc:subfield>";
    protected static final String MARC_DATAFIELD = "<marc:datafield tag=\"100\" ind1=\" \" ind2=\" \">";
    protected static final String MARC_RECORD = "marc:record";
    protected static final String VALIDATION_FOLDER = "validation";
    protected static final String INVALID_SHACL_VALIDATION_SCHEMA_JSON = "invalidDatatypeRangeShaclValidationSchema.json";
    protected static final String VALID_SHACL_VALIDATION_SCHEMA_JSON = "validShaclValidationSchema.json";
    protected static final String ENTITY_EXAMPLE_FILE = "src/test/resources/testdata/example_entity.%s";
    protected static final String REGISTRY_METADATA_EXAMPLE_FILE = "src/test/resources/example_registry.%s";
    protected static String REGISTRY_PATH = "/registry";
    protected static String validValidationSchema;
    protected final SampleData sampleData = new SampleData();
    protected String apiAdminKey;
    protected String registryAdminKey;

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

        TableDriver tableDriver = new TableDriver(client);
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

    protected Response getRegistry(String registryName, String mediaType) throws Exception {
        return target(String.format("/registry/%s", registryName)).request()
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).accept(mediaType).get();
    }

    protected Response readEntity(String registryName, String entityId, String mediaType) {
        return target(String.format("/registry/%s/entity/%s", registryName, entityId)).request()
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).accept(mediaType).get();
    }

    protected Response putSchema(String registryName, String schemaAsJson) {
        return target(String.format("/registry/%s/schema", registryName)).request()
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, registryAdminKey)
            .put(javax.ws.rs.client.Entity.entity(schemaAsJson, MediaType.APPLICATION_JSON));
    }

    protected Response createRegistry(String registryName, String apiKey) {
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        return createRegistry(registryDto, apiKey);
    }

    protected Response createRegistry(RegistryDto registryDto, String apiKey) {
        
        Response response = target("/registry").request().accept(MediaType.APPLICATION_JSON)
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiKey)
            .post(javax.ws.rs.client.Entity.entity(registryDto, MediaType.APPLICATION_JSON));
        return response;
    }

    protected String createRegistry() throws Exception {
        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        createRegistry(registryDto, apiAdminKey);
        return registryName;
    }

    protected Response insertEntryRequest(String registryName, EntityDto entityDto, String apiKey) {
        String path = String.format("/registry/%s/entity", registryName);
        return target(path).request().header(ApiKeyConstants.API_KEY_PARAM_NAME, apiKey)
            .post(javax.ws.rs.client.Entity.entity(entityDto, MediaType.APPLICATION_JSON));
    }

    protected Response createEntity(String registryName) throws IOException {
        EntityDto entity = sampleData.sampleEntityDto();
        Response writeResponse = insertEntryRequest(registryName, entity, apiAdminKey);
        return writeResponse;
    }


    protected Response readEntityWithEntityTag(String registryName, String entityId, EntityTag entityTag) {
        return target(String.format("/registry/%s/entity/%s", registryName, entityId)).request()
            .header("If-None-Match", "\"" + entityTag.getValue() + "\"")
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).get();
    }

    protected Response registryStatus(String registryName) {
        Response response = target(String.format("/registry/%s/status", registryName)).request().get();
        return response;
    }

    protected Response updateEntityRequest(String registryName, EntityDto entityDto) {
        String path = String.format("/registry/%s/entity/%s", registryName, entityDto.getId());
        return target(path).request().header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
            .put(javax.ws.rs.client.Entity.entity(entityDto, MediaType.APPLICATION_JSON));
    }

    protected List<EntityDto> createSampleEntities() throws IOException {
        List<EntityDto> sampleEntities = new CopyOnWriteArrayList<EntityDto>();
        sampleEntities.add(createSampleEntity(UUID.randomUUID().toString()));
        sampleEntities.add(createSampleEntity(UUID.randomUUID().toString()));
        sampleEntities.add(createSampleEntity(UUID.randomUUID().toString()));

        return sampleEntities;
    }

    protected EntityDto createSampleEntity(String identifier) throws IOException {
        EntityDto sampleEntityDto = sampleData.sampleEntityDto();
        sampleEntityDto.setId(identifier);
        return sampleEntityDto;
    }

    protected Response uploadEntities(String registryName, List<EntityDto> sampleEntities) {

        String path = String.format("/registry/%s/upload", registryName);
        return target(path).request(MediaType.APPLICATION_JSON).header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
            .post(javax.ws.rs.client.Entity.entity(sampleEntities, MediaType.APPLICATION_JSON));
    }

    protected Response getEntityAsJson(String registryName, String id) throws Exception {
        return target(String.format("/registry/%s/entity/%s", registryName, id)).request()
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).accept(MediaType.APPLICATION_JSON).get();
    }

    protected Response readSchema(String registryName) throws Exception {
        return target(String.format("/registry/%s/schema", registryName)).request()
            .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).get();
    }

    protected Response replaceApiKey(String registryName, String oldApiKey) {
        String path = String.format("/registry/%s/apikey", registryName);
        return target(path).request().header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey)
            .put(javax.ws.rs.client.Entity.entity(oldApiKey, MediaType.APPLICATION_JSON));
    }
}
