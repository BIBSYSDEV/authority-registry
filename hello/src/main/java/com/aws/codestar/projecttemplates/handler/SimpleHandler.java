package com.aws.codestar.projecttemplates.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.aws.codestar.projecttemplates.GatewayResponse;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
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

    String inputString=readInput(input);
    logger.log(inputString);
    JsonFactory jsonFactory=new JsonFactory();
    ObjectMapper mapper=new ObjectMapper(jsonFactory);
    JsonNode node = mapper.readTree(new StringReader(inputString));
    JsonNode body = node.get("body");
    SimpleRequest request=mapper.readValue(body.asText(),SimpleRequest.class);
    String name=request.getName();

    logger.log(name);
    String response = new SimpleResponse("hello").toGatewayResponse();
    BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(output));
    writer.write(response);
    writer.close();


  }


  private String readInput(InputStream stream) throws IOException {
    BufferedReader reader=new BufferedReader(new InputStreamReader(stream));
    StringBuffer output=new StringBuffer();
    String line=reader.readLine();
    while(line!=null){
      output.append(line);
      line=reader.readLine();
    }
    return output.toString();
  }

}
