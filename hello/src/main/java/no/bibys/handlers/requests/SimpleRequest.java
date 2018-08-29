package no.bibys.handlers.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SimpleRequest {

  private String name;
  private Integer age;



  public SimpleRequest(){}

  public SimpleRequest(String name,Integer age){
    this.name=name;
    this.age=age;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SimpleRequest)) {
      return false;
    }

    SimpleRequest that = (SimpleRequest) o;

    if (name != null ? !name.equals(that.name) : that.name != null) {
      return false;
    }
    return age != null ? age.equals(that.age) : that.age == null;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (age != null ? age.hashCode() : 0);
    return result;
  }


  public String toJson() throws JsonProcessingException {
    ObjectMapper mapper=new ObjectMapper();
    String json=mapper.writeValueAsString(this);
    return json;
  }




}
