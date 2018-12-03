package no.bibsys.web.model;

import java.io.IOException;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BaseConverter {

    protected static final ObjectMapper mapper = new ObjectMapper();
    
    protected static Map<String,Object> toMap(ObjectNode objectNode) {
        @SuppressWarnings("unchecked")
        Map<String,Object> map = mapper.convertValue(objectNode, Map.class);
        return map;
    }
    
    protected static ObjectNode toObjectNode(Map<String,Object> map) {
        return mapper.convertValue(map, ObjectNode.class);    
    }
    
    protected static JsonNode toJsonNode(ObjectNode objectNode) {
        if (objectNode == null) {
            return null;
        }
        try {
            return mapper.readTree(mapper.writeValueAsString(objectNode));
        } catch (IOException e) {
            throw new IllegalArgumentException("Error parsing ObjectNode into JsonNode");
        }       
    }
    
    protected static ObjectNode toObjectNode(JsonNode jsonNode) {
        if (jsonNode == null) {
            return null;
        }
        return mapper.convertValue(jsonNode, ObjectNode.class);    
    }
    
    
    protected static String toJson(ObjectNode objectNode) {
        if (objectNode == null) {
            return null;
        }
        try {
            return mapper.writeValueAsString(objectNode);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error parsing ObjectNode into Json");

        }   

    }

    protected static ObjectNode toObjectNode(String json) {
        if (json == null) {
            return null;
        }
        try {
            return mapper.readValue(json, ObjectNode.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error parsing Json into ObjectNode");
        }     
    }
}
