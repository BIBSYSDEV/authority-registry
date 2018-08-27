package com.aws.codestar.projecttemplates.handler;

public class SimpleResponse {

  private String message;


  public SimpleResponse(){};
  public SimpleResponse(String message){
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


}
