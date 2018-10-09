package no.bibsys.controllers;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.core.Response.Status;
import no.bibsys.handlers.CreateRegistryRequest;
import no.bibsys.handlers.EmptyRegistryRequest;
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
        SimpleResponse expected = new SimpleResponse(
            String.format("A registry with name %s has been created", tableName));

        MvcResult result = createTable(tableName);

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
        createTable(tableName).getResponse();
        MockHttpServletResponse response = createTable(tableName).getResponse();
        assertThat(response.getStatus(), is(equalTo(Status.CONFLICT.getStatusCode())));
    }

    @Test
    @DirtiesContext
    public void databaseControllerShouldInsertEntryInTable() throws Exception {
        createTable(tableName);
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
        createTable(tableName);

        Entry entry = sampleEntry("entryId");
        MockHttpServletResponse response1 = insertEntryRequest(tableName, entry.jsonString())
            .getResponse();
        assertThat(response1.getStatus(), is(equalTo(Status.OK.getStatusCode())));
        MockHttpServletResponse response2 = insertEntryRequest(tableName, entry.jsonString())
            .getResponse();
        assertThat(response2.getStatus(), is(equalTo(Status.CONFLICT.getStatusCode())));
        SimpleResponse response = mapper
            .readValue(response2.getContentAsString(), SimpleResponse.class);
        SimpleResponse expectedResponse = new SimpleResponse(
            String.format("Item %s already exists", entry.id));
        assertThat(response, is(equalTo(expectedResponse)));


    }


    @Test
    @DirtiesContext
    public void databaseControllerShouldDeleteAnExistingRegistry() throws Exception {
        createTable(tableName);

        MvcResult result = mockMvc.perform(delete("/registry/" + tableName)
            .contentType(ContentType.APPLICATION_JSON.toString())
        ).andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));
        SimpleResponse responseBody = mapper
            .readValue(response.getContentAsString(), SimpleResponse.class);
        SimpleResponse expected = new SimpleResponse(
            String.format("Registry %s has been deleted", tableName));
        assertThat(responseBody, is(equalTo(expected)));

    }


    @Test
    @DirtiesContext
    public void databaseControllerShouldReturnErrorWhenDeletingNonExistingRegistry()
        throws Exception {

        MvcResult result = mockMvc.perform(delete("/registry/" + tableName)
            .contentType(ContentType.APPLICATION_JSON.toString())
        ).andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertThat(response.getStatus(), is(equalTo(Status.NOT_FOUND.getStatusCode())));
//        SimpleResponse responseBody=mapper.readValue(response.getContentAsString(),SimpleResponse.class);
//        SimpleResponse expected=new SimpleResponse(String.format("Registry %s has been deleted",tableName));
//        assertThat(responseBody,is(equalTo(expected)));

    }


    @Test
    @DirtiesContext
    public void databaseControllerShouldEmptyAnExistingTable() throws Exception {
        createTable(tableName);
        String entry = sampleEntry("entryId").jsonString();

        insertEntryRequest(tableName, entry);
        EmptyRegistryRequest emptyRegistryRequest = new EmptyRegistryRequest(tableName);
        String jsonRequest = mapper.writeValueAsString(emptyRegistryRequest);

        MockHttpServletResponse httpResponse = mockMvc.perform(post("/registry/")
            .contentType(ContentType.APPLICATION_JSON.toString())
            .content(jsonRequest)).andReturn().getResponse();

        String responseBody = httpResponse.getContentAsString();
        SimpleResponse response = mapper.readValue(responseBody, SimpleResponse.class);
        SimpleResponse expected = new SimpleResponse(
            String.format("Registry %s has been emptied", tableName));
        assertThat(response, is(equalTo(expected)));
    }


    private MvcResult insertEntryRequest(String registryName, String jsonBody)
        throws Exception {
        String path = String.format("/registry/%s/", registryName);
        return mockMvc.perform(post(path)
            .contentType(ContentType.APPLICATION_JSON.toString())
            .content(jsonBody)).andReturn();

    }


    private MvcResult createTable(String tableName) throws Exception {
        CreateRegistryRequest createRequest = new CreateRegistryRequest(tableName);
        createRequest.setValidationSchema("ValidationSchema");
        return createTableRequest(createRequest);
    }


    private MvcResult createTableRequest(CreateRegistryRequest request) throws Exception {
        return mockMvc.perform(put("/registry/" + request.getRegistryName())
            .contentType(ContentType.APPLICATION_JSON.toString())
            .content(request.getValidationSchema()))
            .andReturn();
    }


}
