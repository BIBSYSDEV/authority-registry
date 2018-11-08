package no.bibsys.amazon.handlers;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import no.bibsys.amazon.handlers.events.CodePipelineEvent;
import no.bibsys.amazon.handlers.responses.SimpleResponse;
import no.bibsys.utils.IoUtils;
import org.junit.Test;

public class InitHandlerTest {


    @Test
    public void InitHandlerShouldReturnThePipelineId() throws IOException, URISyntaxException {
        InitHandler initHandler = new InitHandler();
        String eventJson = IoUtils
            .resourceAsString(Paths.get("events", "mock_codePipeline_event.json"));
        CodePipelineEvent event = CodePipelineEvent.create(eventJson);
        SimpleResponse output = initHandler
            .processInput(event, null);

        assertThat(output.getMessage(), is((equalTo("a0a4b321-beb6-4da6-a595-dab82e23de40"))));
    }

}
