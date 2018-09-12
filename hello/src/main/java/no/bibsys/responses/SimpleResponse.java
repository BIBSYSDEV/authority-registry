package no.bibsys.responses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

public class SimpleResponse {

  private String message;


  public SimpleResponse() {
  }


  public SimpleResponse(String message) {
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

  public String toGatewayResponse() throws JsonProcessingException {
    String body = new JSONObject().put("message", getMessage()).toString();
    GatewayResponse response = new GatewayResponse(body);
    ObjectMapper mapper = new ObjectMapper();
    String responseStr = mapper.writeValueAsString(response);
    return responseStr;

  }


}
