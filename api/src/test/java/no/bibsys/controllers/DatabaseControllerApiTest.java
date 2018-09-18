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


    public void init() {
        httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
    }


    @Test
    public void greetingShouldReturnDefaultMessage() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
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
        String tableName = "createTableAPITest";
        ObjectMapper mapper = new ObjectMapper();

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
        ObjectMapper mapper = new ObjectMapper();

        CreateRegistryRequest request = new CreateRegistryRequest(tableName);
        String requestJson = mapper.writeValueAsString(request);
        createTableRequest(requestJson).getResponse();
        MockHttpServletResponse response = createTableRequest(requestJson).getResponse();
        assertThat(response.getStatus(), is(equalTo(Status.CONFLICT.getStatusCode())));
    }

    private MvcResult createTableRequest(String requestJson) throws Exception {
        return mockMvc.perform(post("/registry/create/")
            .contentType(ContentType.APPLICATION_JSON.toString())
            .content(requestJson))
            .andReturn();
    }


}
