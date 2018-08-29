package no.bibys.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import no.bibys.handlers.requests.SimpleRequest;
import no.bibys.handlers.responses.SimpleResponse;


public class SimpleHandler   implements RequestStreamHandler {


  private  HandlerHelper<SimpleRequest,SimpleResponse> helper=
      new HandlerHelper<SimpleRequest,SimpleResponse>(
      SimpleRequest.class,SimpleResponse.class) {

    @Override
    public SimpleResponse processInput(SimpleRequest input) {
      String name=input.getName();
      Integer age=input.getAge();
      String message=String.format("Hello %s. Are you %d years old?",name,age);
      return new SimpleResponse(message);

    }
  };



  @Override
  public void handleRequest(InputStream input, OutputStream output, Context context)
      throws IOException {
    helper.init(input,output,context);
    SimpleRequest request=helper.parseInput(input);
    SimpleResponse response=helper.processInput(request);
    helper.writeOutput(response);

  }


}
