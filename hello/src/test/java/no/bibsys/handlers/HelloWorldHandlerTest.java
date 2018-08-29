package no.bibsys.handlers;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import no.bibsys.utils.IOTestUtils;
import no.bibsys.utils.ReadableOutputStream;
import no.bibys.handlers.HelloWorldHandler;
import no.bibys.handlers.responses.GatewayResponse;
import no.bibys.handlers.responses.SimpleResponse;
import no.bibys.utils.ApiMessageParser;
import no.bibys.utils.IOUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link HelloWorldHandler}. Modify the tests in order to support your use case as you
 * build your project.
 */
//@DisplayName("Tests for HelloWorldHandler")
public class HelloWorldHandlerTest implements IOTestUtils {

  private static final String EXPECTED_CONTENT_TYPE = "application/json";
  private static final String EXPECTED_RESPONSE_VALUE = "Hello World!";
  private static final int EXPECTED_STATUS_CODE_SUCCESS = 200;

  // A mock class for com.amazonaws.services.lambda.runtime.Context
  private final MockLambdaContext mockLambdaContext = new MockLambdaContext();
  IOUtils ioUtils = new IOUtils();
  private ApiMessageParser<SimpleResponse> outputMessageParser = new ApiMessageParser<>();

  /**
   * Basic test to verify the result obtained when calling {@link HelloWorldHandler} successfully.
   */
  @Test
  @DisplayName("Basic test for request handler")
  public void testHandleRequest() throws IOException {
    String input = "";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes());
    ReadableOutputStream ros = ReadableOutputStream.create();
    new HelloWorldHandler().handleRequest(inputStream,
        ros.outputStream,
        mockLambdaContext);

    String outputString = ioUtils.readerToString(ros.reader);
    ObjectMapper mapper = new ObjectMapper();


    SimpleResponse outputMessage = outputMessageParser
        .getBodyElementFromJson(outputString, SimpleResponse.class);

    // Verify the response obtained matches the values we expect.
    JSONObject jsonObjectFromResponse = new JSONObject(outputString);
    assertEquals(EXPECTED_RESPONSE_VALUE, outputMessage.getMessage());
//    assertEquals(EXPECTED_CONTENT_TYPE, gatewayResponse.getHeaders().get("Content-Type"));
//    assertEquals(EXPECTED_STATUS_CODE_SUCCESS, gatewayResponse.getStatusCode());
  }
}
