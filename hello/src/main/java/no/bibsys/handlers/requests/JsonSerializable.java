package no.bibsys.handlers.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface JsonSerializable {


  default String toJson() throws JsonProcessingException {
    ObjectMapper mapper=new ObjectMapper();
    String json=mapper.writeValueAsString(this);
    return json;
  }


}
