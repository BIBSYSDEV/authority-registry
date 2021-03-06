package no.bibsys.web;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.bibsys.db.helpers.AwsResourceGroupsTaggingApiMockBuilder;
import no.bibsys.utils.IoUtils;
import no.bibsys.web.exception.validationexceptionmappers.ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeExceptionMapper;
import no.bibsys.web.model.EntityDto;
import no.bibsys.web.model.RegistryDto;
import no.bibsys.web.model.RegistryInfoNoMetadataDto;
import no.bibsys.web.model.RegistryInfoUiSchemaDto;
import no.bibsys.web.security.ApiKeyConstants;

public class RegistryDatabaseResourceTest extends DatabaseResourceTest {

    private static final String INVALID_APIKEY = "invalidAPIKEY";
    private static final String UPDATED_PUBLISHER_VALUE = "Updated publisher value";
    private static final String PUBLISHER = "Publisher";
    Logger logger = LoggerFactory.getLogger(RegistryDatabaseResourceTest.class);

    @Test
    public void createRegistry_RegistryNotExistingUserNotAuthorized_StatusForbidden() {
        String registryName = UUID.randomUUID().toString();
        Response response = createRegistry(registryName, "InvalidApiKey");
        assertThat(response.getStatus(), is(equalTo(Status.FORBIDDEN.getStatusCode())));
    }

    @Test
    public void createRegistry_RegistryNotExistingWrongUser_StatusForbidden() {
        String registryName = UUID.randomUUID().toString();

        Response response = createRegistry(registryName, registryAdminKey);
        assertThat(response.getStatus(), is(equalTo(Status.FORBIDDEN.getStatusCode())));
    }

    @Test
    public void createRegistry_RegistryNotExistingUserAuthorized_StatusOK() {
        System.out.println("calling ping");
        ping_ReturnsStatusCodeOK();
        System.out.println("called ping");

        String registryName = "TheRegistryName";
        RegistryDto expectedRegistry = sampleData.sampleRegistryDto(registryName);
        Response response = createRegistry(expectedRegistry, apiAdminKey);

        RegistryInfoNoMetadataDto actualRegistry = response.readEntity(RegistryInfoNoMetadataDto.class);

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
        assertThat(actualRegistry.getId(), is(equalTo(expectedRegistry.getId())));
    }

    @Test
    public void createRegistry_RegistryAlreadyExistsUserAuthorized_ReturnsStatusConflict() {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);
        Response response = createRegistry(registryName, apiAdminKey);
        assertThat(response.getStatus(), is(equalTo(Status.CONFLICT.getStatusCode())));
    }

    @Test
    public void putRegistrySchema_NonEmptyRegistry_ReturnsStatusMethodNotAllowed() throws Exception {
        String registryName = createRegistry();
        putSchema(registryName, validValidationSchema);

        EntityDto entity = sampleData.sampleEntityDto("https://example.org/12");
        insertEntryRequest(registryName, entity, apiAdminKey);

        String schemaAsJson = "Schema as Json";
        Response putRegistrySchemaResponse = putSchema(registryName, schemaAsJson);
        assertThat(putRegistrySchemaResponse.getStatus(), is(equalTo(Status.METHOD_NOT_ALLOWED.getStatusCode())));
    }

    @Test
    public void putRegistryUiSchema_NonEmptyRegistry_ReturnsStatusOk() throws Exception {
        String registryName = createRegistry();
        putUiSchema(registryName, validUiSchema);
        
        EntityDto entity = sampleData.sampleEntityDto("https://example.org/12");
        insertEntryRequest(registryName, entity, apiAdminKey);
        
        String schemaAsJson = "Schema as Json";
        Response putRegistrySchemaResponse = putUiSchema(registryName, schemaAsJson);
        assertThat(putRegistrySchemaResponse.getStatus(), is(equalTo(Status.OK.getStatusCode())));
    }
    
    @Test
    public void putRegistrySchema_RegistryExistsValidSchema_ReturnsStatusOK() {
        String registryName = createRegistry();

        Response putRegistrySchemaResponse = putSchema(registryName, validValidationSchema);
        assertThat(putRegistrySchemaResponse.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        Response response = readSchema(registryName);
        RegistryInfoNoMetadataDto registry = response.readEntity(RegistryInfoNoMetadataDto.class);
        assertThat(validValidationSchema, is(equalTo(registry.getSchema())));
    }
    
    @Test
    public void putUiSchema_RegistryExistsValidSchema_ReturnsStatusOK() {
        String registryName = createRegistry();
        
        Response putRegistrySchemaResponse = putUiSchema(registryName, validUiSchema);
        assertThat(putRegistrySchemaResponse.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        Response response = readUiSchema(registryName);
        RegistryInfoUiSchemaDto registry = response.readEntity(RegistryInfoUiSchemaDto.class);
        assertThat(validUiSchema, is(equalTo(registry.getUischema())));
    }

    @Test
    public void putRegistrySchema_RegistryExistsInvalidSchema_ReturnsStatusBadRequest() throws Exception {
        String registryName = createRegistry();

        Response putRegistrySchemaResponse = putSchema(registryName, validValidationSchema);
        assertThat(putRegistrySchemaResponse.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        String invalidSchema = IoUtils.resourceAsString(
                Paths.get(VALIDATION_FOLDER, INVALID_SHACL_VALIDATION_SCHEMA_JSON));
        Response invalidSchemaResponse = putSchema(registryName, invalidSchema);
        String message = invalidSchemaResponse.readEntity(String.class);
        assertThat(message, is(equalTo(ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeExceptionMapper.MESSAGE)));
    }

    @Test
    public void getRegistryStatus_registryExists_returnsStatusCreated() {
        String registryName = createRegistry();

        Response response = registryStatus(registryName);
        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
    }

    @Test
    public void replaceApiKey_registryExists_returnsNewApiKey() {
        AwsResourceGroupsTaggingApiMockBuilder awsResourceGroupsTaggingApiMockBuilder =
                new AwsResourceGroupsTaggingApiMockBuilder();
        awsResourceGroupsTaggingApiMockBuilder.withMatchableResourceTagMapping(null);
        awsResourceGroupsTaggingApiMockBuilder.build().initialize();

        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        Response createRegistryResponse = createRegistry(registryDto, apiAdminKey);

        RegistryInfoNoMetadataDto newRegistry = createRegistryResponse.readEntity(RegistryInfoNoMetadataDto.class);
        String oldApiKey = newRegistry.getApiKey();

        Response newApiKeyResponse = replaceApiKey(registryName, oldApiKey);
        String newApiKey = newApiKeyResponse.readEntity(String.class);

        assertThat(newApiKey, is(not(equalTo(oldApiKey))));
    }

    @Test
    public void replaceApiKey_registryNotExisting_returnsStatusNotFound() {
        String registryName = UUID.randomUUID().toString();
        String oldApiKey = UUID.randomUUID().toString(); // random non-existing apikey

        Response newApiKeyResponse = replaceApiKey(registryName, oldApiKey);

        assertThat(newApiKeyResponse.getStatusInfo(), is(equalTo(Status.NOT_FOUND)));
    }

    @Test
    public void replaceApiKey_RegistryExistingWrongApiKey_ReturnsStatusBadRequest() {
        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        createRegistry(registryDto, apiAdminKey);
        String oldApiKey = UUID.randomUUID().toString(); // random non-existing apikey

        Response newApiKeyResponse = replaceApiKey(registryName, oldApiKey);

        assertThat(newApiKeyResponse.getStatusInfo(), is(equalTo(Status.BAD_REQUEST)));
    }

    @Test
    public void getRegistryMetadata_textHtml_registryAsHtml() {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);

        Response entityAsHtml = getRegistry(registryName, MediaType.TEXT_HTML);
        String html = entityAsHtml.readEntity(String.class);

        assertThat(html, containsString("html"));
        assertThat(html, containsString("<title>Registry name value</title>"));
        assertThat(html, containsString("data-automation-id=\"Registry_name\""));
        assertThat(html, containsString("data-automation-id=\"Publisher\""));
    }

    @Test
    public void updateRegistryMetadata_registryExists_returnsUpdatedRegistryMetadata() {
        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        createRegistry(registryDto, apiAdminKey);

        registryDto.getMetadata().put(PUBLISHER, UPDATED_PUBLISHER_VALUE);
        Response updateRegistryResponse = updateRegistry(registryDto, apiAdminKey);
        assertThat(updateRegistryResponse.getStatusInfo(), is(Status.ACCEPTED));

        Response getRegistryResponse = getRegistry(registryName, MediaType.APPLICATION_JSON);
        String responseString = getRegistryResponse.readEntity(String.class);
        assertThat(responseString, containsString(UPDATED_PUBLISHER_VALUE));
    }

    @Test
    public void updateRegistryMetadata_registryNotExists_returnsNotFound() {
        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);

        Response updateRegistryResponse = updateRegistry(registryDto, apiAdminKey);
        assertThat(updateRegistryResponse.getStatusInfo(), is(Status.NOT_FOUND));
    }

    @Test
    public void listRegistries_registryExists_returnsRegistryList() {
        String registryName1 = UUID.randomUUID().toString();
        RegistryDto registryDto1 = sampleData.sampleRegistryDto(registryName1);
        createRegistry(registryDto1, apiAdminKey);

        String registryName2 = UUID.randomUUID().toString();
        RegistryDto registryDto2 = sampleData.sampleRegistryDto(registryName2);
        createRegistry(registryDto2, apiAdminKey);

        Response response = target("/registry").request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).get();
        @SuppressWarnings("unchecked") List<String> registryList = response.readEntity(List.class);
        assertThat(registryList.size(), is(2));
        assertThat(registryList.contains(registryName1), is(true));
        assertThat(registryList.contains(registryName2), is(true));
    }

    private Response updateRegistry(RegistryDto registryDto, String apiKey) {
        return target("/registry/" + registryDto.getId()).request().accept(MediaType.APPLICATION_JSON).header(
                ApiKeyConstants.API_KEY_PARAM_NAME, apiKey).put(
                javax.ws.rs.client.Entity.entity(registryDto, MediaType.APPLICATION_JSON));
    }

    @Test
    public void deleteRegistry_RegistryExistsUserAuthorized_ReturnsStatusOk() {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);
        putSchema(registryName, validValidationSchema);

        Response response = target("/registry/" + registryName).request().header(ApiKeyConstants.API_KEY_PARAM_NAME,
                apiAdminKey).delete();

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
        String entity = response.readEntity(String.class);
        String expected = String.format("Registry %s has been deleted", registryName);
        assertThat(entity, is(equalTo(expected)));
    }

    @Test
    public void deleteRegistry_RegistryExistsUserNotAuthorized_ReturnsStatusForbidden() {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);

        Response response = target("/registry/" + registryName).request().header(ApiKeyConstants.API_KEY_PARAM_NAME,
                INVALID_APIKEY).delete();

        assertThat(response.getStatus(), is(equalTo(Status.FORBIDDEN.getStatusCode())));
    }

    @Test
    public void deleteRegistry_RegistryNotExisting_ReturnsStatusNotFound() {

        String registryName = UUID.randomUUID().toString();
        Response response = target("/registry/" + registryName).request().header(ApiKeyConstants.API_KEY_PARAM_NAME,
                apiAdminKey).delete();

        String entity = response.readEntity(String.class);
        assertThat(response.getStatus(), is(equalTo(Status.NOT_FOUND.getStatusCode())));

        String expected = String.format("Registry with name %s does not exist", registryName);
        assertThat(entity, is(equalTo(expected)));
    }

    @Test
    public void callEndpoint_WrongRole_ReturnsStatusForbidden() {
        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        Response response = target("/registry").request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, registryAdminKey)
                .post(javax.ws.rs.client.Entity.entity(registryDto.toString(), MediaType.APPLICATION_JSON));

        assertThat(response.getStatus(), is(equalTo(Status.FORBIDDEN.getStatusCode())));
    }

    @Test
    public void getRegistryMetadata_RegistryExists_ReturnsStatusOk() {

        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);

        createRegistry(registryDto, apiAdminKey);

        Response response = target(String.format("/registry/%s", registryName)).request().header(
                ApiKeyConstants.API_KEY_PARAM_NAME, apiAdminKey).accept(MediaType.TEXT_HTML).get();

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
    }
}
