package no.bibsys.web;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

import java.nio.file.Paths;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import no.bibsys.utils.IoUtils;
import no.bibsys.web.exception.validationexceptionmappers.ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeExceptionMapper;
import no.bibsys.web.model.EntityDto;
import no.bibsys.web.model.RegistryDto;
import no.bibsys.web.model.RegistryInfoNoMetadataDto;
import no.bibsys.web.security.ApiKeyConstants;

public class RegistryDatabaseResourceTest extends DatabaseResourceTest {

    private static final String UPDATED_PUBLISHER_VALUE = "Updated publisher value";
    private static final String PUBLISHER = "Publisher";

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
        Response response = createRegistry(expectedRegistry, apiAdminKey);

        RegistryInfoNoMetadataDto actualRegistry = response.readEntity(RegistryInfoNoMetadataDto.class);

        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
        assertThat(actualRegistry.getId(), is(equalTo(expectedRegistry.getId())));
    }

    @Test
    public void createRegistry_RegistryAlreadyExistsUserAuthorized_ReturnsStatusConflict() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);
        Response response = createRegistry(registryName, apiAdminKey);
        assertThat(response.getStatus(), is(equalTo(Status.CONFLICT.getStatusCode())));
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
        RegistryInfoNoMetadataDto registry = response.readEntity(RegistryInfoNoMetadataDto.class);
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
    public void getRegistryStatus_registryExists_returnsStatusCreated() throws Exception {
        String registryName = createRegistry();

        Response response = registryStatus(registryName);
        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
    }

    @Test
    public void replaceApiKey_registryExists_returnsNewApiKey() throws Exception {
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
    public void replaceApiKey_registryNotExisting_returnsStatusNotFound() throws Exception {
        String registryName = UUID.randomUUID().toString();
        String oldApiKey = UUID.randomUUID().toString(); // random non-existing apikey

        Response newApiKeyResponse = replaceApiKey(registryName, oldApiKey);

        assertThat(newApiKeyResponse.getStatusInfo(), is(equalTo(Status.NOT_FOUND)));
    }

    @Test
    public void replaceApiKey_RegistryExistingWrongApiKey_ReturnsStatusBadRequest() throws Exception {
        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        createRegistry(registryDto, apiAdminKey);
        String oldApiKey = UUID.randomUUID().toString(); // random non-existing apikey

        Response newApiKeyResponse = replaceApiKey(registryName, oldApiKey);

        assertThat(newApiKeyResponse.getStatusInfo(), is(equalTo(Status.BAD_REQUEST)));
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
    
    @Test
    public void updateRegistryMetadata_registryExists_returnsUpdatedRegistryMetadata() throws Exception {
        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        createRegistry(registryDto, apiAdminKey);

        registryDto.getMetadata().put(PUBLISHER, UPDATED_PUBLISHER_VALUE);
        Response updateRegistryResponse = updateRegistry(registryDto, apiAdminKey);
        assertThat(updateRegistryResponse.getStatusInfo(), is(Status.OK));
        
        Response getRegistryResponse = getRegistry(registryName, MediaType.APPLICATION_JSON);
        RegistryDto updatedRegistryDto = getRegistryResponse.readEntity(RegistryDto.class);

    }
    
    @Test
    public void updateRegistryMetadata_registryNotExists_returnsNotFound() {
        
    }

    private Response updateRegistry(RegistryDto registryDto, String apiKey) {
        Response response = target("/registry").request().accept(MediaType.APPLICATION_JSON)
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiKey)
                .put(javax.ws.rs.client.Entity.entity(registryDto, MediaType.APPLICATION_JSON));
        return response;
    }
}
