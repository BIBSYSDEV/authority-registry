package com.aws.codestar.projecttemplates.handler;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class DBHandler implements RequestHandler<SimpleRequest,SimpleResponse> {

  @Override
  public SimpleResponse handleRequest(SimpleRequest input, Context context) {
      String name=input.getName();
      Integer age=input.getAge();
      return new SimpleResponse(name);
  }


//  public void storeInDatabase(){
//    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
//  }
}
