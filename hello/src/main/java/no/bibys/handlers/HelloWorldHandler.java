package no.bibys.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import no.bibys.handlers.responses.SimpleResponse;

/**
 * Handler for requests to Lambda function.
 */
public class HelloWorldHandler implements RequestStreamHandler {


    private HandlerHelper<String, SimpleResponse> handlerHelper=new HandlerHelper<String, SimpleResponse>
        (String.class,SimpleResponse.class) {
        @Override
        SimpleResponse processInput(String input) {
            return new SimpleResponse("Hello World!");
        }
    };


    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context)
        throws IOException {
        handlerHelper.init(input,output,context);
        String inputString=handlerHelper.parseInput(input);
        SimpleResponse outputMessage = handlerHelper.processInput(inputString);
        handlerHelper.writeOutput(outputMessage);
    }
}
