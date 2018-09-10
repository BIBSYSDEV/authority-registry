//package no.bibsys.handlers;
//
//import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
//import no.bibsys.handlers.requests.SimpleRequest;
//import no.bibsys.handlers.responses.SimpleResponse;
//
//
//public class SimpleHandler extends HandlerHelper<SimpleRequest,SimpleResponse>  implements RequestStreamHandler {
//
//
//  public SimpleHandler() {
//    super(SimpleRequest.class, SimpleResponse.class);
//  }
//
//  @Override
//  public SimpleResponse processInput(SimpleRequest input) {
//    String name=input.getName();
//    Integer age=input.getAge();
//    String message=String.format("Hello %s. Are you %d years old?",name,age);
//    return new SimpleResponse(message);
//
//  }
//}
