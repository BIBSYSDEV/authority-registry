package com.aws.codestar.projecttemplates.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link HelloWorldHandler}. Modify the tests in order to support your use case as you
 * build your project.
 */
@DisplayName("Tests for HelloWorldHandler")
public class SimpleHandlerTest {

  private static final String EXPECTED_CONTENT_TYPE = "application/json";
  private static final String EXPECTED_RESPONSE_VALUE = "{\"message\":\"Hello orestis!, Are you 15 years old?\"}";
  private static final int EXPECTED_STATUS_CODE_SUCCESS = 200;

  // A mock class for com.amazonaws.services.lambda.runtime.Context
  private final MockLambdaContext mockLambdaContext = new MockLambdaContext();
  private final SimpleRequest input = new SimpleRequest("orestis", 15);

  /**
   * Initializing variables before we run the tests. Use @BeforeAll for initializing static
   * variables at the start of the test class execution. Use @BeforeEach for initializing variables
   * before each test is run.
   */
  @BeforeAll
  static void setup() {
    // Use as needed.
  }

  /**
   * De-initializing variables after we run the tests. Use @AfterAll for de-initializing static
   * variables at the end of the test class execution. Use @AfterEach for de-initializing variables
   * at the end of each test.
   */
  @AfterAll
  static void tearDown() {
    // Use as needed.
  }

  /**
   * Basic test to verify the result obtained when calling {@link HelloWorldHandler} successfully.
   */
  @Test
  @DisplayName("Basic test for request handler")
  void testHandleRequest() throws IOException {
    String json = input.toJson();
    ByteArrayInputStream inputStream=new ByteArrayInputStream(json.getBytes());
    PipedOutputObject outputObject=newPipedOutputObject();
    new SimpleHandler().handleRequest(inputStream, outputObject.outputStream, mockLambdaContext);

    String response=readerToString(outputObject.reader);
    System.out.print(response);
    // Verify the response obtained matches the values we expect.
//        JSONObject jsonObjectFromResponse = new JSONObject(response.getBody());
//    assertEquals(EXPECTED_RESPONSE_VALUE, response.getBody());
//    assertEquals(EXPECTED_CONTENT_TYPE, response.getHeaders().get("Content-Type"));
//    assertEquals(EXPECTED_STATUS_CODE_SUCCESS, response.getStatusCode());
  }

  private   PipedOutputObject newPipedOutputObject() throws IOException {
    PipedInputStream inputStream = new PipedInputStream();
    PipedOutputStream outputStream = new PipedOutputStream(inputStream);

    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    return new PipedOutputObject(inputStream,outputStream,reader);
  }


  private String readerToString(BufferedReader reader) throws IOException {
    StringBuffer stringBuffer=new StringBuffer();
    String line=reader.readLine();
    while(line!=null){
      stringBuffer.append(line);
      line=reader.readLine();
    }
    return stringBuffer.toString();
  }


  private class PipedOutputObject {

    public final PipedInputStream inputStream;
    public final PipedOutputStream outputStream;
    public final BufferedReader reader;

    public PipedOutputObject(PipedInputStream inputStream,
        PipedOutputStream outputStream,
        BufferedReader reader) {
      this.inputStream = inputStream;
      this.outputStream = outputStream;
      this.reader = reader;

    }



  }

}
