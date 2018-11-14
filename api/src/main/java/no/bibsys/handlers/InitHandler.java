package no.bibsys.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import no.bibsys.amazon.handlers.events.buildevents.BuildEvent;
import no.bibsys.amazon.handlers.responses.SimpleResponse;
import no.bibsys.amazon.handlers.templates.CodePipelineFunctionHandlerTemplate;

public class InitHandler extends CodePipelineFunctionHandlerTemplate<SimpleResponse> {

//    private final transient AuthenticationService authenticationService;




    @Override
    public SimpleResponse processInput(BuildEvent input, Context context) {

//        authenticationService.createApiKeyTable();
//        authenticationService.setUpInitialApiKeys();

        return new SimpleResponse("Initializing!!");


    }


}
