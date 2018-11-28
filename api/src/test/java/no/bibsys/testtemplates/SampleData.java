package no.bibsys.testtemplates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import no.bibsys.db.Entity;


public class SampleData {

    public SampleData() {}

    private final ObjectMapper mapper = new ObjectMapper();

    public Entity sampleEntity() throws JsonProcessingException {

        ObjectNode root = mapper.getNodeFactory().objectNode();
        root.put("label", "A random label");
        root.put("number", 5);
        ArrayNode array = root.putArray("myArray");
        array.add(1);
        array.add(2);
        array.add(3);
        
        String json = mapper.writeValueAsString(root);
        
        Entity entity = new Entity(json);

        String id = "sampleId";
        entity.setId(id);
        
        return entity;
    }

}
