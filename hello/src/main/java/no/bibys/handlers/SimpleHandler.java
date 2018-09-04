package no.bibys.handlers;

import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import no.bibys.handlers.requests.SimpleRequest;
import no.bibys.handlers.responses.SimpleResponse;


public class SimpleHandler extends HandlerHelper<SimpleRequest,SimpleResponse>  implements RequestStreamHandler {


  public SimpleHandler(Class<SimpleRequest> iclass,
      Class<SimpleResponse> oclass) {
    super(iclass, oclass);
  }

  @Override
  public SimpleResponse processInput(SimpleRequest input) {
    String name=input.getName();
    Integer age=input.getAge();
    String message=String.format("Hello %s. Are you %d years old?",name,age);
    return new SimpleResponse(message);

  }
}
