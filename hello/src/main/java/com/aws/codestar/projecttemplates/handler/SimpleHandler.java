package com.aws.codestar.projecttemplates.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public class SimpleHandler implements RequestHandler<SimpleRequest, SimpleResponse> {

  @Override
  public SimpleResponse handleRequest(SimpleRequest input, Context context) {
    String name=input.getName();
    Integer age=input.getAge();
    String message=String.format("Hello %s!, Are you %d years old?",name,age);
    return new SimpleResponse(message);




  }
}
