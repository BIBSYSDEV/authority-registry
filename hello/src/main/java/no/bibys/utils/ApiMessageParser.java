package no.bibys.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiMessageParser<T> {

  Logger logger=LoggerFactory.getLogger(ApiMessageParser.class);

  public T getBodyElementFromJson(String inputString, Class<T> tClass) throws IOException {
    JsonFactory jsonFactory = new JsonFactory();
    ObjectMapper mapper = new ObjectMapper(jsonFactory);
    Optional<JsonNode> tree = Optional.ofNullable(mapper.readTree(new StringReader(inputString)));
    Optional<JsonNode> body = tree.map(node -> node.get("body"));
    Optional<T> request=body.map(b->parseBody(mapper,b.asText(),tClass));
    return request.orElse(null);

  }


  private T parseBody(ObjectMapper mapper, String json, Class<T> tclass) {
    try {
      T object = mapper.readValue(json, tclass);
      return object;
    } catch (IOException e) {
      logger.error("Error parsing json string:{}",json);
      logger.error(e.getMessage());
      return null;
    }
  }
}