package no.bibys.handlers;

import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import no.bibys.handlers.responses.SimpleResponse;

/**
 * Handler for requests to Lambda function.
 */
public class HelloWorldHandler extends HandlerHelper<String,SimpleResponse> implements RequestStreamHandler {


    public HelloWorldHandler() {
        super(String.class, SimpleResponse.class);
    }

    @Override
    public SimpleResponse processInput(String input) {
            return new SimpleResponse("Hello World!");
    }


}
