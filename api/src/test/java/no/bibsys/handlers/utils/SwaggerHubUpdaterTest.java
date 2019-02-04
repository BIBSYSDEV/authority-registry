package no.bibsys.handlers.utils;

import static no.bibsys.handlers.utils.SwaggerHubUpdater.SERVERS_FIELD;
import static no.bibsys.handlers.utils.SwaggerHubUpdater.URL_FIELD;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

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
import no.bibsys.aws.apigateway.ServerInfo;
import no.bibsys.aws.cloudformation.Stage;
import org.junit.Test;

public class SwaggerHubUpdaterTest {

    private static final String STACK_NAME_VALUE = "the_stack";
    private static final String BRANCH = "testBranch";

    private static final transient String SWAGGER_ID = "aut-reg-service";
    private static final transient String API_VERSION = "1.0";
    private static final transient String SWAGGERHUB_ONWER = "randomOwner";

    private final ObjectMapper jsonParser = Json.mapper();
    private final ServerInfo serverInfo = new ServerInfo("http://localhost", Stage.TEST.toString());
    private final transient SwaggerHubUpdater swaggerHubUpdater;

    public SwaggerHubUpdaterTest() {
        this.swaggerHubUpdater = new SwaggerHubUpdater(SWAGGER_ID, API_VERSION, SWAGGERHUB_ONWER, STACK_NAME_VALUE,
            Stage.TEST, BRANCH);
    }

    @Test
    public void serversNode_serverInfo_ObjectNodeWithCorrectServerInformation() throws IOException, URISyntaxException {
        ArrayNode serversNode = swaggerHubUpdater.serversNode(serverInfo);
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
        ObjectNode updatedApiRoot = swaggerHubUpdater.updateSwaggerHubDocWithServerInfo(openApiRoot, serverInfo);
        assertThat(updatedApiRoot.has(SERVERS_FIELD), is(equalTo(true)));
        assertThat(updatedApiRoot.get(SERVERS_FIELD).get(0).get(URL_FIELD).asText(),
            is(equalTo(serverInfo.getServerUrl())));
    }
}
