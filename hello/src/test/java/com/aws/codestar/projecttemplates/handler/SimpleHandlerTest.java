package com.aws.codestar.projecttemplates.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.aws.codestar.projecttemplates.GatewayResponse;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link HelloWorldHandler}. Modify the tests in order to support your use case as you build your project.
 */
@DisplayName("Tests for HelloWorldHandler")
public class SimpleHandlerTest {

    private static final String EXPECTED_CONTENT_TYPE = "application/json";
    private static final String EXPECTED_RESPONSE_VALUE = "Hello orestis!, Are you 15 years old?";
    private static final int EXPECTED_STATUS_CODE_SUCCESS = 200;

    // A mock class for com.amazonaws.services.lambda.runtime.Context
    private final MockLambdaContext mockLambdaContext = new MockLambdaContext();
    private final SimpleRequest input = new SimpleRequest("orestis",15);

    /**
     * Initializing variables before we run the tests.
     * Use @BeforeAll for initializing static variables at the start of the test class execution.
     * Use @BeforeEach for initializing variables before each test is run.
     */
    @BeforeAll
    static void setup() {
        // Use as needed.
    }

    /**
     * De-initializing variables after we run the tests.
     * Use @AfterAll for de-initializing static variables at the end of the test class execution.
     * Use @AfterEach for de-initializing variables at the end of each test.
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
    void testHandleRequest() {
        SimpleResponse response = new SimpleHandler().handleRequest(input, mockLambdaContext);

        // Verify the response obtained matches the values we expect.
//        JSONObject jsonObjectFromResponse = new JSONObject(response.getBody());
        assertEquals(EXPECTED_RESPONSE_VALUE, response.getMessage());
        assertEquals(EXPECTED_CONTENT_TYPE, response.getHeaders().get("Content-Type"));
        assertEquals(EXPECTED_STATUS_CODE_SUCCESS, response.getStatusCode());
    }
}
