package no.bibsys.handlers;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.nio.file.Paths;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import no.bibsys.EnvironmentVariables;
import no.bibsys.aws.cloudformation.Stage;
import no.bibsys.aws.lambda.events.DeployEvent;
import no.bibsys.aws.lambda.events.DeployEventBuilder;
import no.bibsys.aws.lambda.responses.SimpleResponse;
import no.bibsys.utils.IoUtils;


public class InitHandlerTest {


    private no.bibsys.aws.tools.Environment env;



    public InitHandlerTest(){
        env= Mockito.mock(no.bibsys.aws.tools.Environment.class);
        when(env.readEnv((EnvironmentVariables.CERTIFICATE_ARN_ENV))).thenReturn("somthing goes here");
        when(env.readEnv((EnvironmentVariables.APPLICATION_URL))).thenReturn("apihello.entitydata.aws.unit.no.");
        when(env.readEnv((EnvironmentVariables.STACK_NAME))).thenReturn("aut-reg-autre-88-stati-url-service-stack-test");
        when(env.readEnv(EnvironmentVariables.STAGE_NAME)).thenReturn(Stage.TEST.toString());
        when(env.readEnv(EnvironmentVariables.API_KEY_TABLE_NAME)).thenReturn("aut-reg-autre-88-stati-url-test-apiKeys");

        when(env.readEnv((EnvironmentVariables.HOSTED_ZONE_NAME))).thenReturn("aws.unit.no.");


    }

    @Test
    @Ignore
    public void initHandler_WhenProcessingInput_ReturnsThePipelineId() throws IOException {
        InitHandler initHandler = new InitHandler(env);

        String eventJson =
                IoUtils.resourceAsString(Paths.get("events", "mock_codePipeline_event.json"));
        DeployEvent event = DeployEventBuilder.create(eventJson);
        SimpleResponse output = initHandler.processInput(event, eventJson, null);

        assertThat(output.getMessage(), is((equalTo("a0a4b321-beb6-4da6-a595-dab82e23de40"))));
    }

}
