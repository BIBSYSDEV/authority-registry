package no.bibys.handlers;

import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import no.bibys.handlers.requests.SimpleRequest;
import no.bibys.handlers.responses.SimpleResponse;


public class DatabaseHandler extends HandlerHelper<SimpleRequest, SimpleResponse> implements
    RequestStreamHandler {


  public DatabaseHandler() {
    super(SimpleRequest.class, SimpleResponse.class);
  }


  @Override
  SimpleResponse processInput(SimpleRequest input) {
    return null;
  }
}