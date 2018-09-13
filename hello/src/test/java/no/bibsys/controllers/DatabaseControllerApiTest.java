package no.bibsys.controllers;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.core.Response.Status;
import no.bibsys.LocalDynamoConfiguration;
import no.bibsys.handlers.CreateRegistryRequest;
import no.bibsys.responses.SimpleResponse;
import org.apache.http.entity.ContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@WebMvcTest
@DirtiesContext
@ContextConfiguration(classes = {LocalDynamoConfiguration.class})

//@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
//@ContextConfiguration(classes = {LocalDynamoConfiguration.class})
public class DatabaseControllerApiTest {


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
    String message = new String(response.getContentAsByteArray());
    SimpleResponse actual = mapper.readValue(message, SimpleResponse.class);

    assertThat(actual, is(equalTo(expected)));

  }


  @Test
  @DirtiesContext
  public void DatabaseControllerShouldSendSuccessWhenCreatingNonExistingTable() throws Exception {
    String tableName = "createTableAPITest";
    ObjectMapper mapper = new ObjectMapper();

    CreateRegistryRequest request = new CreateRegistryRequest(tableName);
    String requestJson = mapper.writeValueAsString(request);
    SimpleResponse expected = new SimpleResponse(
        String.format("The registry name is %s", tableName));

    MvcResult result = createTableRequest(requestJson);

    MockHttpServletResponse response = result.getResponse();
    String message = new String(response.getContentAsByteArray());
    assertThat(response.getStatus(), is(equalTo(Status.OK.getStatusCode())));

    SimpleResponse actual = mapper.readValue(message, SimpleResponse.class);
    assertThat(actual, is(equalTo(expected)));

  }

  @Test
  @DirtiesContext
  public void DatabaseControllerShouldSendConflictWhenCreatingExistingTable() throws Exception {
    String tableName = "createTableAPITest";
    ObjectMapper mapper = new ObjectMapper();

    CreateRegistryRequest request = new CreateRegistryRequest(tableName);
    String requestJson = mapper.writeValueAsString(request);
    SimpleResponse expected = new SimpleResponse(
        String.format("The registry name is %s", tableName));

    createTableRequest(requestJson).getResponse();

    MockHttpServletResponse response = createTableRequest(requestJson).getResponse();

    assertThat(response.getStatus(), is(equalTo(Status.CONFLICT.getStatusCode())));
//    String message = new String(response.getContentAsByteArray());
//    SimpleResponse actual = mapper.readValue(message, SimpleResponse.class);

//    assertThat(actual,is(equalTo(expected)));

  }

  private MvcResult createTableRequest(String requestJson) throws Exception {
    return mockMvc.perform(post("/registry/create/")
        .contentType(ContentType.APPLICATION_JSON.toString())
        .content(requestJson))
        .andReturn();
  }


}
