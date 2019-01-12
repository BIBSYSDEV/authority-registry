package no.bibsys.web.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import no.bibsys.utils.JsonUtils;

public class BaseConverter {

    protected static final ObjectMapper mapper = JsonUtils.newJsonParser();
    
    protected static Map<String, Object> toMap(ObjectNode objectNode) {
        @SuppressWarnings("unchecked")
        Map<String,Object> map = mapper.convertValue(objectNode, Map.class);
        return map;
    }
    
    protected static ObjectNode toObjectNode(Map<String,Object> map) {
        return mapper.convertValue(map, ObjectNode.class);    
    }
    
    protected static JsonNode toJsonNode(ObjectNode objectNode) {
        try {
            return mapper.readTree(mapper.writeValueAsString(objectNode));
        } catch (IOException e) {
            throw new IllegalArgumentException("Error parsing ObjectNode into JsonNode");
        }       
    }
    
    protected static String toJson(ObjectNode objectNode) {
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
 
    protected static ObjectNode toObjectNode(JsonNode jsonNode) {
        if (jsonNode == null) {
            return null;
        }
        return mapper.convertValue(jsonNode, ObjectNode.class);
    }
}
