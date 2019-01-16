package no.bibsys.handlers;

import static no.bibsys.handlers.InitHandler.BASE_PATH_FIELD;
import static no.bibsys.handlers.InitHandler.DEFAULT_FIELD;
import static no.bibsys.handlers.InitHandler.SERVERS_FIELD;
import static no.bibsys.handlers.InitHandler.URL_FIELD;
import static no.bibsys.handlers.InitHandler.VARIABLES_FIELD;
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
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import no.bibsys.aws.apigateway.ServerInfo;
import no.bibsys.aws.cloudformation.Stage;
import no.bibsys.aws.tools.Environment;
import no.bibsys.aws.tools.IoUtils;
import no.bibsys.aws.tools.JsonUtils;
import org.junit.Test;
import org.mockito.Mockito;

public class InitHandlerTest {


    public static final String STAGE = "STAGE";

    private final transient InitHandler initHandler;
    private final String openApiString;
    private final ObjectMapper yamlParser = JsonUtils.newYamlParser();
    private final ServerInfo serverInfo=new ServerInfo("http://localhost",Stage.TEST.toString());


    public InitHandlerTest() throws IOException {
        Environment environment= Mockito.mock(Environment.class);
        when(environment.readEnv(anyString())).thenAnswer(invocation->{
                String input=invocation.getArgument(0);
                if(input.toUpperCase().contains(STAGE)){
                  return Stage.TEST.toString();
                }

                return invocation.getArgument(0);

            });

        initHandler=new InitHandler(environment);
        openApiString= IoUtils.resourceAsString(Paths.get("openapi","openapi.yaml"));
    }




    @Test
    public void readSwaggerFile_gradleGeneratedFile_ObjectNode() throws IOException {

        ArrayNode serversNode = initHandler.serversNode(serverInfo);

        ObjectNode root= yamlParser.createObjectNode();
        root.set(SERVERS_FIELD,serversNode);
        String yamlString= yamlParser.writeValueAsString(root);

        JsonNode expectedRoot = yamlParser.readTree(yamlString);

        assertThat(expectedRoot.isObject(),is(equalTo(true)));

        assertTrue(expectedRoot.has(SERVERS_FIELD));
        assertTrue(expectedRoot.get(SERVERS_FIELD).isArray());
        assertTrue(expectedRoot.get(SERVERS_FIELD).get(0).isObject());
        assertTrue(expectedRoot.get(SERVERS_FIELD).get(0).has(URL_FIELD));
        assertTrue(expectedRoot.get(SERVERS_FIELD).get(0).get(URL_FIELD).isTextual());
        assertTrue(expectedRoot.get(SERVERS_FIELD).get(0).has(VARIABLES_FIELD));
        assertTrue(expectedRoot.get(SERVERS_FIELD).get(0).get(VARIABLES_FIELD).has(BASE_PATH_FIELD));
        assertTrue(expectedRoot.get(SERVERS_FIELD).get(0).get(VARIABLES_FIELD)
            .get(BASE_PATH_FIELD).has(DEFAULT_FIELD));
        assertThat(expectedRoot.get(SERVERS_FIELD).get(0).get(VARIABLES_FIELD)
            .get(BASE_PATH_FIELD).get(DEFAULT_FIELD).asText(),is(equalTo(Stage.TEST.toString())));


    }


    @Test
    public void updateSwaggerHubDocWithServerInfo_swaggerFile_swaggerFileWithServerInfo()
        throws IOException, URISyntaxException, OpenApiConfigurationException {
        ObjectNode openApiRoot=(ObjectNode)yamlParser.readTree(openApiString);

        ObjectNode updatedApiRoot = initHandler
            .updateSwaggerHubDocWithServerInfo(openApiRoot, serverInfo);

        initHandler.updateSwaggerHub();
        assertThat(updatedApiRoot.has(SERVERS_FIELD),is(equalTo(true)));
        assertThat(updatedApiRoot.get(SERVERS_FIELD).get(0).get(URL_FIELD).asText(),is(equalTo(serverInfo.getServerUrl())));
        assertThat(updatedApiRoot.get(SERVERS_FIELD).get(0).get(VARIABLES_FIELD)
            .get(BASE_PATH_FIELD)
            .get(DEFAULT_FIELD)
            .asText(),is(equalTo(serverInfo.getStage())));
    }


}
