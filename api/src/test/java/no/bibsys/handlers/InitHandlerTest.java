package no.bibsys.handlers;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import org.junit.Ignore;
import org.junit.Test;
import no.bibsys.amazon.handlers.events.buildevents.BuildEvent;
import no.bibsys.amazon.handlers.events.buildevents.BuildEventBuilder;
import no.bibsys.amazon.handlers.responses.SimpleResponse;
import no.bibsys.utils.IoUtils;

public class InitHandlerTest {


    // TODO create Integration.class template
    @Test
    @Ignore
    public void initHandler_WhenProcessingInput_ReturnsThePipelineId()
            throws IOException, URISyntaxException {
        InitHandler initHandler = new InitHandler();
        String eventJson =
                IoUtils.resourceAsString(Paths.get("events", "mock_codePipeline_event.json"));
        BuildEvent event = BuildEventBuilder.create(eventJson);
        SimpleResponse output = initHandler.processInput(event, null);

        assertThat(output.getMessage(), is((equalTo("a0a4b321-beb6-4da6-a595-dab82e23de40"))));
    }

}
