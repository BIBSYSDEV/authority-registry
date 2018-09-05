package no.bibsys.handlers;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import no.bibsys.utils.IOUtils;
import no.bibsys.utils.ReadableOutputStream;
import no.bibys.handlers.HelloWorldHandler;
import no.bibys.handlers.SimpleHandler;
import no.bibys.handlers.responses.SimpleResponse;
import no.bibys.utils.ApiMessageParser;
import org.json.JSONObject;
import org.junit.Ignore;

/**
 * Tests for {@link HelloWorldHandler}. Modify the tests in order to support your use case as you
 * build your project.
 */
public class SimpleHandlerTest  {

  private static final String EXPECTED_CONTENT_TYPE = "application/json";
  private static final String EXPECTED_BODY_VALUE = "{\"message\":\"Hello orestis. Are you 15 years old?\"}";
  private static final String EXPECTED_RESPONSE_VALUE = "Hello orestiss. Are you 15 years old?";
  private static final int EXPECTED_STATUS_CODE_SUCCESS = 200;

  // A mock class for com.amazonaws.services.lambda.runtime.Context
  private final MockLambdaContext mockLambdaContext = new MockLambdaContext();

  private IOUtils ioUtils=new IOUtils();
  private ApiMessageParser<SimpleResponse> responseParser=new ApiMessageParser<>();
  private ObjectMapper objectMapper=new ObjectMapper();


  /**
   * Basic test to verify the result obtained when calling {@link HelloWorldHandler} successfully.
   */
  @Ignore
  public  void testHandleRequest() throws IOException {
    String json = ioUtils.resourceAsString(Paths.get("api", "apiInput.txt"));

    ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
    ReadableOutputStream ros = ReadableOutputStream.create();

    SimpleHandler handler = new SimpleHandler();
    handler.handleRequest(inputStream, ros.outputStream, mockLambdaContext);
    String outputString = ioUtils.readerToString(ros.reader);


    SimpleResponse response = responseParser
        .getBodyElementFromJson(outputString, SimpleResponse.class);
    JSONObject jsonObject=new JSONObject(outputString);


    assertThat(response.getMessage(), is(equalTo(EXPECTED_RESPONSE_VALUE)));
    assertThat(jsonObject.get("body"),is(equalTo(EXPECTED_BODY_VALUE)));

  }





}
