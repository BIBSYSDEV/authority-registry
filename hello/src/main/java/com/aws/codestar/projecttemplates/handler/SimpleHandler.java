package com.aws.codestar.projecttemplates.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class SimpleHandler implements RequestHandler<SimpleRequest, SimpleResponse> {

  public static final Logger logger=LoggerFactory.getLogger(SimpleHandler.class);

  @Override
  public SimpleResponse handleRequest(SimpleRequest input, Context context) {
    logger.info(input.getClass().getName());
    String name=input.getName();
    Integer age=input.getAge();
    String message=String.format("Hello %s!, Are you %d years old?",name,age);

    return new SimpleResponse(message);




  }
}
