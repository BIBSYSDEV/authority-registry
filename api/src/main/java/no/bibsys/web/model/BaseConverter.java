package no.bibsys.web.model;


import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import no.bibsys.utils.JsonUtils;

public class BaseConverter {

    protected static final ObjectMapper mapper = JsonUtils.newJsonParser();

    protected static Map<String, Object> toMap(ObjectNode objectNode) {
        @SuppressWarnings("unchecked") Map<String, Object> map = mapper.convertValue(objectNode, Map.class);
        return map;
    }

    protected static String toJson(ObjectNode objectNode) throws JsonProcessingException {
            return mapper.writeValueAsString(objectNode);
    }

    protected static ObjectNode toObjectNode(Map<String, Object> map) {
        return mapper.convertValue(map, ObjectNode.class);
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
