package no.bibsys.web.model;

import java.io.IOException;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BaseConverter {

    protected static final ObjectMapper mapper = new ObjectMapper();
    
    protected static Map<String,Object> toMap(ObjectNode objectNode) {
        try {
            @SuppressWarnings("unchecked")
            Map<String,Object> map = mapper.readValue(mapper.writeValueAsString(objectNode), Map.class);
            return map;
        } catch (IOException e) {
            throw new IllegalArgumentException("Error parsing ObjectNode into Map");
        }
    }
    
    protected static ObjectNode toObjectNode(Map<String,Object> map) {
        try {
            return mapper.readValue(mapper.writeValueAsString(map), ObjectNode.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error parsing Map into ObjectNode");
        }
    }
    
    protected static JsonNode toJsonNode(ObjectNode objectNode) {
        try {
            return mapper.readValue(mapper.writeValueAsString(objectNode), JsonNode.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error parsing ObjectNode into JsonNode");
        }       
    }
    
    protected static ObjectNode toObjectNode(JsonNode jsonNode) {
        try {
            return mapper.readValue(mapper.writeValueAsString(jsonNode), ObjectNode.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error parsing ObjectNode into JsonNode");
        }       
    }
}
