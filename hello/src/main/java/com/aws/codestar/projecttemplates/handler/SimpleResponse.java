package com.aws.codestar.projecttemplates.handler;

import java.util.HashMap;
import java.util.Map;

public class SimpleResponse {

   String message;
   Map<String,String> headers;
   int statusCode;



  public SimpleResponse(
  ){
    headers=new HashMap<>();
    headers.put("Content-Type", "application/json");
    this.statusCode=200;
  };
  public SimpleResponse(String message){
    this();
    setMessage(message);
  }



  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SimpleResponse)) {
      return false;
    }

    SimpleResponse that = (SimpleResponse) o;

    return message != null ? message.equals(that.message) : that.message == null;
  }

  @Override
  public int hashCode() {
    return message != null ? message.hashCode() : 0;
  }


  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
  public Map<String, String> getHeaders() {
    return headers;
  }

  public void setHeaders(Map<String, String> headers) {
    this.headers = headers;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

}
