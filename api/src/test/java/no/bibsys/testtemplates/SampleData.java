package no.bibsys.testtemplates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import no.bibsys.web.model.EntityDto;
import no.bibsys.web.model.RegistryDto;


public class SampleData {

    public SampleData() {}

    public EntityDto sampleEntityDto() throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

//        Map<String,Object> body = new HashMap<>();
//        body.put("label", "A random label");
//        body.put("number", 5);
//        body.put("myArray", Arrays.asList(1,2,3));
        
        
        ObjectNode body = mapper.createObjectNode();
        body.put("label", "A random label");
        body.put("number", 5);
        ArrayNode array = body.putArray("myArray");
        array.add(1);
        array.add(2);
        array.add(3);

//        JsonObject body = new JsonObject();
//        body.addProperty("label", "A random label");
//        body.addProperty("number", 5);
//        body.addProperty("boolean", true);
//        body.add("emptyArray", new JsonArray());
//        JsonArray array = new JsonArray();
//        array.add(1);
//        array.add(2);
//        array.add(3);
//        body.add("myArray", array);
        
        EntityDto entityDto = new EntityDto();

        String id = "sampleId";
        entityDto.setId(id);
        entityDto.setBody(body);
        
        return entityDto;
    }

    public RegistryDto sampleRegistryDto(String registryName) {
        
        RegistryDto registryDto = new RegistryDto();
        registryDto.setId(registryName);
        registryDto.setMetadata(NullNode.getInstance());
        
        return registryDto;
    }

}
