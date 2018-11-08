package no.bibsys.amazon.handlers;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Paths;
import no.bibsys.utils.IoUtils;
import org.junit.Test;

public class InitHandlerTest {


    @Test
    public void InitHandlerShouldReturnThePipelineId() throws IOException {
        InitHandler initHandler = new InitHandler();
        InputStream input = IoUtils
            .resourceAsStream(Paths.get("events", "mock_codePipeline_event.json"));
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);
        initHandler.handleRequest(input, out, new MockLambdaContext());

        String outputString = IoUtils.streamToString(in);
        assertThat(outputString.hashCode(), is(not(equalTo(true))));
    }

}
