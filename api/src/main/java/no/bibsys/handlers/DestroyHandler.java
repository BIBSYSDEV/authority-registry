package no.bibsys.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import no.bibsys.amazon.handlers.events.buildevents.BuildEvent;
import no.bibsys.amazon.handlers.responses.SimpleResponse;
import no.bibsys.amazon.handlers.templates.CodePipelineFunctionHandlerTemplate;

public class DestroyHandler extends CodePipelineFunctionHandlerTemplate<SimpleResponse> {


    @Override
    public SimpleResponse processInput(BuildEvent input, Context context) {

        return new SimpleResponse("Destroyinng!!");


    }


}
