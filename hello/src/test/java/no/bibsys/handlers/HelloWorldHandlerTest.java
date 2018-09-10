//package no.bibsys.handlers;

//
//import static org.junit.Assert.assertEquals;
//
//import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import no.bibsys.utils.IOUtils;
//import no.bibsys.utils.ReadableOutputStream;
//import no.bibsys.handlers.responses.GatewayResponse;
//import no.bibsys.handlers.responses.SimpleResponse;
//import no.bibsys.utils.ApiMessageParser;
//import org.junit.Ignore;
//
//
///**
// * Tests for {@link HelloWorldHandler}. Modify the tests in order to support your use case as you
// * build your project.
// */
////@DisplayName("Tests for HelloWorldHandler")
//public class HelloWorldHandlerTest  {
//
//  private static final String EXPECTED_CONTENT_TYPE = "application/json";
//  private static final String EXPECTED_BODY_VALUE = "{\"message\":\"Hello World!\"}";
//  private static final String EXPECTED_MESSAGE_VALUE = "Hello World!";
//
//  private static final int EXPECTED_STATUS_CODE_SUCCESS = 200;
//
//  // A mock class for com.amazonaws.services.lambda.runtime.Context
//  private final MockLambdaContext mockLambdaContext = new MockLambdaContext();
//
//  private IOUtils ioUtils = new IOUtils();
//  private ApiMessageParser<SimpleResponse> outputMessageParser = new ApiMessageParser<>();
//  private ObjectMapper mapper=new ObjectMapper();
//
//
//  /**
//   * Basic test to verify the result obtained when calling {@link HelloWorldHandler} successfully.
//   */
//
//  @Ignore
//  public void testHandleRequest() throws IOException {
//    String input = "";
//    InputStream inputStream = new ByteArrayInputStream(input.getBytes());
//    ReadableOutputStream ros = ReadableOutputStream.create();
//    new HelloWorldHandler().handleRequest(inputStream,
//        ros.outputStream,
//        mockLambdaContext);
//
//    String outputString = ioUtils.readerToString(ros.reader);
//
//    SimpleResponse outputMessage = outputMessageParser
//        .getBodyElementFromJson(outputString, SimpleResponse.class);
//
//    // Verify the response obtained matches the values we expect.
//    GatewayResponse gatewayResponse= mapper.readValue(outputString,GatewayResponse.class) ;
//
//    assertEquals(EXPECTED_BODY_VALUE, gatewayResponse.getBody());
//    assertEquals(EXPECTED_MESSAGE_VALUE, outputMessage.getMessage());
//    assertEquals(EXPECTED_CONTENT_TYPE, gatewayResponse.getHeaders().get("Content-Type"));
//    assertEquals(EXPECTED_STATUS_CODE_SUCCESS, gatewayResponse.getStatusCode());
//  }
//}
