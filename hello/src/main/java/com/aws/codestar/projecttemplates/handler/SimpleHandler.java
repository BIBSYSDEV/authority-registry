package com.aws.codestar.projecttemplates.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.aws.codestar.projecttemplates.GatewayResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import org.json.JSONObject;



public class SimpleHandler   implements RequestStreamHandler {

    ObjectMapper objectMapper=new ObjectMapper();


//  @Override
//  public GatewayResponse handleRequest(SimpleRequest input, Context context) {
//
//    context.getLogger().log(input.);
//    String name=input.getName();
//    Integer age=input.getAge();
//    String message=String.format("Hello %s!, Are you %d years old?",name,age);
//
//    return new SimpleResponse(message).toGatewayResponse();
//
//
//
//
//  }

  @Override
  public void handleRequest(InputStream input, OutputStream output, Context context)
      throws IOException {

    LambdaLogger logger = context.getLogger();

    BufferedReader reader=new BufferedReader(new InputStreamReader(input));

    HashMap<String, Object> jsonMap = (HashMap<String, Object>) objectMapper
        .readValue(reader, HashMap.class);
    String keys=String.join(",",jsonMap.keySet());
    logger.log(keys);
    String response=new SimpleResponse(keys).toGatewayResponse();
    BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(output));
    writer.write(response);
    writer.close();



  }
}
