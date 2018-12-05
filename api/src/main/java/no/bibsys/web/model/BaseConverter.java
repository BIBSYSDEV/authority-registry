package no.bibsys.web.model;

import java.io.IOException;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

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
        try {
            return mapper.readTree(mapper.writeValueAsString(objectNode));
        } catch (IOException e) {
            throw new IllegalArgumentException("Error parsing ObjectNode into JsonNode");
        }       
    }
    
    protected static ObjectNode toObjectNode(JsonElement jsonNode) {
        return mapper.convertValue(jsonNode, ObjectNode.class);    
    }
    
    
    protected static String toJson(ObjectNode objectNode) {
        try {
            return mapper.writeValueAsString(objectNode);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error parsing ObjectNode into Json");

        }   

    }

    protected static ObjectNode toObjectNode(String json) {
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
    
    protected static ObjectNode toObjectNode(JsonObject jsonObject) {
        try {
            Gson gson = new Gson();
            return mapper.readValue(gson.toJson(jsonObject), ObjectNode.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error parsing JsonObject into ObjectNode");

        }
    }
    
    protected static JsonElement toJsonObject(ObjectNode objectNode) {
        try {
            JsonParser jsonParser = new JsonParser();
            return jsonParser.parse(mapper.writeValueAsString(objectNode));
        } catch (JsonSyntaxException | JsonProcessingException e) {
            throw new IllegalArgumentException("Error parsing ObjectNode into JsonObject");

        }
    }
}
