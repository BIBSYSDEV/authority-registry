package no.bibsys.handlers;

import static no.bibsys.handlers.InitHandler.SERVERS_FIELD;
import static no.bibsys.handlers.InitHandler.URL_FIELD;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.models.OpenAPI;
import java.io.IOException;
import java.net.URISyntaxException;
import no.bibsys.EnvironmentVariables;
import no.bibsys.aws.apigateway.ServerInfo;
import no.bibsys.aws.cloudformation.Stage;
import no.bibsys.aws.tools.Environment;
import org.junit.Test;
import org.mockito.Mockito;

public class InitHandlerTest {

    private static final String STAGE = "STAGE";
    private static final String STACK_NAME_VALUE = "arn:aws:cloudformation:eu-west-1:933878624978:stack/aut-reg"
        + "-jersey-2-author-service-stack-test/759d2d50-18d0-11e9-8173-061b9f50c2ce";
    private final transient InitHandler initHandler;

    private final ObjectMapper jsonParser = Json.mapper();
    private final ServerInfo serverInfo = new ServerInfo("http://localhost", Stage.TEST.toString());


    public InitHandlerTest() {
        Environment environment = Mockito.mock(Environment.class);
        when(environment.readEnv(anyString())).thenAnswer(invocation -> {
            String input = invocation.getArgument(0);
            if (input.toUpperCase().contains(STAGE)) {
                return Stage.TEST.toString();
            } else if (input.equalsIgnoreCase(EnvironmentVariables.STACK_NAME)) {
                return STACK_NAME_VALUE;
            } else if (input.equalsIgnoreCase(EnvironmentVariables.SWAGGER_API_OWNER)) {
                return "randomOwner";
            } else if (input.equalsIgnoreCase(EnvironmentVariables.SWAGGER_API_ID)) {
                return "aut-reg-service";
            } else if (input.equalsIgnoreCase(EnvironmentVariables.SWAGGER_API_VERSION)) {
                return "1.0";
            }
            return invocation.getArgument(0);
        });
        initHandler = new InitHandler(environment);
    }


    @Test
    public void serversNode_serverInfo_ObjectNodeWithCorrectServerInformation() throws IOException, URISyntaxException {
        ArrayNode serversNode = initHandler.serversNode(serverInfo);
        ObjectNode root = jsonParser.createObjectNode();
        root.set(SERVERS_FIELD, serversNode);
        String yamlString = jsonParser.writeValueAsString(root);
        JsonNode expectedRoot = jsonParser.readTree(yamlString);
        assertThat(expectedRoot.isObject(), is(equalTo(true)));
        assertTrue(expectedRoot.has(SERVERS_FIELD));
        assertTrue(expectedRoot.get(SERVERS_FIELD).isArray());
        assertTrue(expectedRoot.get(SERVERS_FIELD).get(0).isObject());
        assertTrue(expectedRoot.get(SERVERS_FIELD).get(0).has(URL_FIELD));
        assertTrue(expectedRoot.get(SERVERS_FIELD).get(0).get(URL_FIELD).isTextual());
    }


    @Test
    public void updateSwaggerHubDocWithServerInfo_swaggerFile_swaggerFileWithServerInfo()
        throws IOException, OpenApiConfigurationException, URISyntaxException {

        OpenAPI openApi = new JaxrsOpenApiContextBuilder().buildContext(true).read();
        String openApiString = Json.pretty(openApi);
        ObjectNode openApiRoot = (ObjectNode) jsonParser.readTree(openApiString);
        ObjectNode updatedApiRoot = initHandler.updateSwaggerHubDocWithServerInfo(openApiRoot, serverInfo);
        assertThat(updatedApiRoot.has(SERVERS_FIELD), is(equalTo(true)));
        assertThat(updatedApiRoot.get(SERVERS_FIELD).get(0).get(URL_FIELD).asText(),
            is(equalTo(serverInfo.getServerUrl())));
    }

}
