package no.bibsys.amazon.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import java.io.IOException;
import java.net.URISyntaxException;
import no.bibsys.amazon.handlers.events.CodePipelineEvent;
import no.bibsys.amazon.handlers.responses.SimpleResponse;
import no.bibsys.amazon.handlers.templates.CodePipelineFunctionHandlerTemplate;

public class InitHandler extends CodePipelineFunctionHandlerTemplate<SimpleResponse> {

    @Override
    public SimpleResponse processInput(CodePipelineEvent input, Context context)
        throws IOException, URISyntaxException {

        String pipelineId = input.getId();
        return new SimpleResponse(pipelineId);


    }


}
