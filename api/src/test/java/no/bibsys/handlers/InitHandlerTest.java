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
import io.swagger.v3.core.util.Json;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.models.OpenAPI;
import java.io.IOException;
import no.bibsys.EnvironmentVariables;
import no.bibsys.aws.apigateway.ServerInfo;
import no.bibsys.aws.cloudformation.Stage;
import no.bibsys.aws.tools.Environment;
import org.junit.Test;
import org.mockito.Mockito;

public class InitHandlerTest {


    public static final String STAGE = "STAGE";
    private static final String STACK_NAME_VALUE= "arn:someStack";
    private final transient InitHandler initHandler;

    private final ObjectMapper jsonParser =Json.mapper();
    private final ServerInfo serverInfo=new ServerInfo("http://localhost",Stage.TEST.toString());



    public InitHandlerTest()  {
        Environment environment= Mockito.mock(Environment.class);
        when(environment.readEnv(anyString())).thenAnswer(invocation->{
                String input=invocation.getArgument(0);
                if(input.toUpperCase().contains(STAGE)){
                  return Stage.TEST.toString();
                }
                else if(input.equalsIgnoreCase(EnvironmentVariables.STACK_NAME)){
                    return STACK_NAME_VALUE;
                }

                return invocation.getArgument(0);

            });

        initHandler=new InitHandler(environment);
    }




    @Test
    public void serversNode_serverInfo_ObjectNodeWithCorrectServerInformation() throws IOException {

        ArrayNode serversNode = initHandler.serversNode(serverInfo);

        ObjectNode root= jsonParser.createObjectNode();
        root.set(SERVERS_FIELD,serversNode);
        String yamlString= jsonParser.writeValueAsString(root);

        JsonNode expectedRoot = jsonParser.readTree(yamlString);

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
        throws IOException, OpenApiConfigurationException {
        OpenAPI openApi = new JaxrsOpenApiContextBuilder()
            .buildContext(true).read();

        String openApiString=Json.pretty(openApi);

        ObjectNode openApiRoot=(ObjectNode) jsonParser.readTree(openApiString);

        ObjectNode updatedApiRoot = initHandler
            .updateSwaggerHubDocWithServerInfo(openApiRoot, serverInfo);


        assertThat(updatedApiRoot.has(SERVERS_FIELD),is(equalTo(true)));
        assertThat(updatedApiRoot.get(SERVERS_FIELD).get(0).get(URL_FIELD).asText(),is(equalTo(serverInfo.getServerUrl())));
        assertThat(updatedApiRoot.get(SERVERS_FIELD).get(0).get(VARIABLES_FIELD)
            .get(BASE_PATH_FIELD)
            .get(DEFAULT_FIELD)
            .asText(),is(equalTo(serverInfo.getStage())));
    }


}
