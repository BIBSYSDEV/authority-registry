package no.bibsys.controllers;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.core.Response.Status;
import no.bibsys.handlers.CreateRegistryRequest;
import no.bibsys.responses.PathResponse;
import no.bibsys.responses.SimpleResponse;
import no.bibsys.testtemplates.ApiTest;
import org.apache.http.entity.ContentType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


public class DatabaseControllerApiTest extends ApiTest {


    @Autowired
    MockMvc mockMvc;
    @Autowired
    DatabaseController databaseController;


    private HttpHeaders httpHeaders;

    String tableName = "DatabaseControllerAPITest";
    private ObjectMapper mapper = new ObjectMapper();

    public void init() {
        httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
    }


    @Test
    public void greetingShouldReturnDefaultMessage() throws Exception {

        SimpleResponse expected = new SimpleResponse("Invalid path");

        MvcResult result = mockMvc.perform(get("/hello"))
            .andReturn();

        MockHttpServletResponse response = result.getResponse();
        String message = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        SimpleResponse actual = mapper.readValue(message, SimpleResponse.class);

        assertThat(actual, is(equalTo(expected)));

    }


    @Test
    @DirtiesContext
    public void databaseControllerShouldSendSuccessWhenCreatingNonExistingTable() throws Exception {
        CreateRegistryRequest request = new CreateRegistryRequest(tableName);
        String requestJson = mapper.writeValueAsString(request);
        SimpleResponse expected = new SimpleResponse(
            String.format("A registry with name %s has been created", tableName));

        MvcResult result = createTableRequest(requestJson);

        MockHttpServletResponse response = result.getResponse();
        String message = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));

        SimpleResponse actual = mapper.readValue(message, SimpleResponse.class);
        assertThat(actual, is(equalTo(expected)));

    }


    @Test
    @DirtiesContext
    public void databaseControllerShouldSendConflictWhenCreatingExistingTable() throws Exception {
        String tableName = "createTableAPITest";

        CreateRegistryRequest request = new CreateRegistryRequest(tableName);
        String requestJson = mapper.writeValueAsString(request);

        createTableRequest(requestJson).getResponse();
        MockHttpServletResponse response = createTableRequest(requestJson).getResponse();
        assertThat(response.getStatus(), is(equalTo(Status.CONFLICT.getStatusCode())));
    }

    @Test
    @DirtiesContext
    public void databaseControllerShouldInsertEntryInTable() throws Exception {
        CreateRegistryRequest createRequest = new CreateRegistryRequest(tableName);
        String requestJson = mapper.writeValueAsString(createRequest);
        createTableRequest(requestJson);

        Entry entry = sampleEntry("entryId");
        MockHttpServletResponse response = insertEntryRequest(tableName, entry.jsonString())
            .getResponse();
        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
        String responseBodyJson = response.getContentAsString();
        PathResponse pathResponse = mapper.readValue(responseBodyJson, PathResponse.class);
        String expectedPath = String.format("/registry/%s/%s", tableName, entry.id);
        assertThat(pathResponse.getPath(), is(equalTo(expectedPath)));
    }


    @Test
    @DirtiesContext
    public void databaseControllerShouldThrowExceptionOnDuplicateEntries() throws Exception {
        CreateRegistryRequest createRequest = new CreateRegistryRequest(tableName);
        String requestJson = mapper.writeValueAsString(createRequest);
        createTableRequest(requestJson);

        Entry entry = sampleEntry("entryId");
        MockHttpServletResponse response1 = insertEntryRequest(tableName, entry.jsonString())
            .getResponse();
        assertThat(response1.getStatus(), is(equalTo(Status.OK.getStatusCode())));
        MockHttpServletResponse response2 = insertEntryRequest(tableName, entry.jsonString())
            .getResponse();
        assertThat(response2.getStatus(), is(equalTo(Status.CONFLICT.getStatusCode())));
        SimpleResponse response = mapper
            .readValue(response2.getContentAsString(), SimpleResponse.class);
        String expectedResponse = String.format("Item %s already exists", entry.id);
        assertThat(response.getMessage(), is(equalTo(expectedResponse)));


    }


    private MvcResult insertEntryRequest(String registryName, String jsonBody)
        throws Exception {
        String path = String.format("/registry/%s/", registryName);
        return mockMvc.perform(post(path)
            .contentType(ContentType.APPLICATION_JSON.toString())
            .content(jsonBody)).andReturn();

    }


    private MvcResult createTableRequest(String requestJson) throws Exception {
        return mockMvc.perform(post("/registry")
            .contentType(ContentType.APPLICATION_JSON.toString())
            .content(requestJson))
            .andReturn();
    }


}
