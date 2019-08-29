package no.bibsys.web;

import com.amazonaws.services.s3.Headers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jsonldjava.core.JsonLdConsts;
import no.bibsys.testtemplates.SampleData;
import no.bibsys.utils.JsonUtils;
import no.bibsys.utils.ModelParser;
import no.bibsys.web.exception.validationexceptionmappers.ValidationSchemaNotFoundExceptionMapper;
import no.bibsys.web.model.CustomMediaType;
import no.bibsys.web.model.EntityDto;
import no.bibsys.web.model.RegistryDto;
import no.bibsys.web.model.RegistryInfoNoMetadataDto;
import no.bibsys.web.security.ApiKeyConstants;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

public class EntityDatabaseResourceTest extends DatabaseResourceTest {

    private static final String ENTITY_SAMPLE_ID = "/entity/sampleId";

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
    public void insertEntity_RegistryExistUserAuthorized_ReturnsStatusCreated() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);
        putSchema(registryName, validValidationSchema);

        String expectedUri = "http://localhost/null/registry/" + registryName + "/entity/sampleId";
        EntityDto expectedEntity = sampleData.sampleEntityDto(expectedUri);
        Response response = insertEntryRequest(registryName, expectedEntity, apiAdminKey);
        EntityDto actualEntity = response.readEntity(EntityDto.class);

        assertThat(response.getStatus(), is(equalTo(Status.CREATED.getStatusCode())));
        assertThat(response.getLocation().toString(), containsString(ENTITY_SAMPLE_ID));

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

        EntityDto expectedEntity = sampleData.sampleEntityDto("https://example.org/21");
        Response response = insertEntryRequest(registryName, expectedEntity, apiAdminKey);
        String message = response.readEntity(String.class);

        assertThat(response.getStatus(), is(equalTo(Status.FORBIDDEN.getStatusCode())));
        assertThat(message, is(equalTo(ValidationSchemaNotFoundExceptionMapper.MESSAGE)));
    }

    @Test
    public void insertEntity_RegistryExistUserNotAuthorized_ReturnsStatusForbidden() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);

        EntityDto expectedEntity = sampleData.sampleEntityDto("https://example.org/21");
        Response response = insertEntryRequest(registryName, expectedEntity, "invalidKey");
        assertThat(response.getStatus(), is(equalTo(Status.FORBIDDEN.getStatusCode())));
    }

    @Test
    public void insertEntity_RegistryExistRegistryAdminUser_ReturnsStatusOk() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);
        putSchema(registryName, validValidationSchema);

        EntityDto expectedEntity = sampleData.sampleEntityDto("https://example.org/21");
        Response response = insertEntryRequest(registryName, expectedEntity, registryAdminKey);
        assertThat(response.getStatus(), is(equalTo(Status.CREATED.getStatusCode())));
        assertThat(response.getLocation().toString(), containsString(ENTITY_SAMPLE_ID));
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
    public void getEntity_applicationMarc_entityAsMarc() throws Exception {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);
        putSchema(registryName, validValidationSchema);
        EntityDto entity = createEntity(registryName).readEntity(EntityDto.class);
        
        Response entityAsmarc = readEntity(registryName, entity.getId(), CustomMediaType.APPLICATION_MARC);
        String marc = entityAsmarc.readEntity(String.class);
        
        assertThat(marc.toLowerCase(), containsString(MARC_RECORD));
        assertThat(marc.toLowerCase(), containsString(MARC_DATAFIELD));
        assertThat(marc.toLowerCase(), containsString(MARC_SUBFIELD));
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
        Model expectedModel = parser.parseModel(getModifiedTestData(testFile, registryName), lang);

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
        Model expectedModel = parser.parseModel(getModifiedTestData(testFile, registryName), lang);

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

        Model expectedModel = parser.parseModel(getModifiedTestData(testFile, registryName), lang);

        assertThat(actualModel.isIsomorphicWith(expectedModel), is(true));
    }

    private InputStream getModifiedTestData(String file, String registryName) throws IOException {
        FileInputStream inputStream = new FileInputStream(new File(file));
        String testData = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        testData = testData.replace("__REPLACE__", "http://localhost/null/registry/" + registryName
                + "/entity/sampleId");
        return IOUtils.toInputStream(testData, StandardCharsets.UTF_8);
    }

    @Test
    public void deleteEntity_entityExists_entityIsDeleted() throws IOException {
        String registryName = UUID.randomUUID().toString();
        Response createRegistryResponse = createRegistry(registryName, apiAdminKey);
        putSchema(registryName, validValidationSchema);
        EntityDto entity = createEntity(registryName).readEntity(EntityDto.class);
        
        RegistryInfoNoMetadataDto registryInfo = createRegistryResponse.readEntity(RegistryInfoNoMetadataDto.class);
        String registryAdminApiKey = registryInfo.getApiKey();
        
        Response deleteEntityResponse = deleteEntity(registryName, entity.getId(), registryAdminApiKey);
        assertThat(deleteEntityResponse.getStatus(), is(Status.OK.getStatusCode()));
        
        Response readEntityResponse = readEntity(registryName, entity.getId(), MediaType.APPLICATION_JSON);
        assertThat(readEntityResponse.getStatus(), is(Status.NOT_FOUND.getStatusCode()));
    }

    @Test
    public void deleteEntity_entityNotExists_returnsNotFound() throws IOException {
        String registryName = UUID.randomUUID().toString();
        Response createRegistryResponse = createRegistry(registryName, apiAdminKey);
        putSchema(registryName, validValidationSchema);
        
        RegistryInfoNoMetadataDto registryInfo = createRegistryResponse.readEntity(RegistryInfoNoMetadataDto.class);
        String registryAdminApiKey = registryInfo.getApiKey();
        
        String entityId = UUID.randomUUID().toString();
        Response deleteEntityResponse = deleteEntity(registryName, entityId, registryAdminApiKey);
        assertThat(deleteEntityResponse.getStatus(), is(Status.NOT_FOUND.getStatusCode()));
    }
    
    @Test
    public void deleteEntity_noAccess_returnsForbidden() throws IOException {
        String registryName = UUID.randomUUID().toString();
        createRegistry(registryName, apiAdminKey);
        putSchema(registryName, validValidationSchema);
        EntityDto entity = createEntity(registryName).readEntity(EntityDto.class);
        
        String notRegistryAdminApiKey = UUID.randomUUID().toString();
        Response deleteEntityResponse = deleteEntity(registryName, entity.getId(), notRegistryAdminApiKey);
        assertThat(deleteEntityResponse.getStatus(), is(Status.FORBIDDEN.getStatusCode()));
    }
    
    @Test
    public void deleteEntity_registryNotExists_returnsForbidden() {
        String registryName = UUID.randomUUID().toString();
        
        String notRegistryAdminApiKey = UUID.randomUUID().toString();
        String notEntityId = UUID.randomUUID().toString();
        Response deleteEntityResponse = deleteEntity(registryName, notEntityId, notRegistryAdminApiKey);
        assertThat(deleteEntityResponse.getStatus(), is(Status.FORBIDDEN.getStatusCode()));
    }
    
    private Response deleteEntity(String registryName, String id, String apiKey) {
        Response deleteResponse = target("/registry/" + registryName + "/entity/" + id).request()
                .header(ApiKeyConstants.API_KEY_PARAM_NAME, apiKey)
                .delete();
        return deleteResponse;
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
        EntityDto updatedEntity = updatedSampleData.sampleEntityDto("https://example.org/21");
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            numberOfEntities.set(numberOfEntities.incrementAndGet());
        });

        assertThat(numberOfEntities.get(), is(equalTo(3)));
    }
    
    @Test
    public void uploadArrayOfThreeEntities_RegistryNotExisting_ReturnsStatusNotFound() throws Exception {

        String registryName = UUID.randomUUID().toString();
        List<EntityDto> sampleEntities = createSampleEntities();

        Response response = uploadEntities(registryName, sampleEntities);
        assertThat(response.getStatus(), is(equalTo(Status.NOT_FOUND.getStatusCode())));
    }
}
