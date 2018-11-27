package no.bibsys.handlers;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import no.bibsys.aws.lambda.handlers.events.DeployEvent;
import no.bibsys.aws.lambda.handlers.events.DeployEventBuilder;
import no.bibsys.aws.lambda.responses.SimpleResponse;
import no.bibsys.utils.IoUtils;
import org.junit.Ignore;
import org.junit.Test;

public class InitHandlerTest {


    // TODO create Integration.class template
    @Test
    @Ignore
    public void initHandler_WhenProcessingInput_ReturnsThePipelineId() throws IOException, URISyntaxException {
        InitHandler initHandler = new InitHandler();
        String eventJson =
                IoUtils.resourceAsString(Paths.get("events", "mock_codePipeline_event.json"));
        DeployEvent event = DeployEventBuilder.create(eventJson);
        SimpleResponse output = initHandler.processInput(event, eventJson, null);

        assertThat(output.getMessage(), is((equalTo("a0a4b321-beb6-4da6-a595-dab82e23de40"))));
    }

}
