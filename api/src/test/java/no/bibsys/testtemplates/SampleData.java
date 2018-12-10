package no.bibsys.testtemplates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import no.bibsys.web.model.EntityDto;
import no.bibsys.web.model.RegistryDto;


public class SampleData {

    public SampleData() {}

    public EntityDto sampleEntityDto() throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode body = mapper.createObjectNode();
        body.put("label", "A random label");
        body.put("number", 5);
        ArrayNode array = body.putArray("myArray");
        array.add(1);
        array.add(2);
        array.add(3);
        
        EntityDto entityDto = new EntityDto();

        String id = "sampleId";
        entityDto.setId(id);
        entityDto.setBody(mapper.writeValueAsString(body));
        
        return entityDto;
    }

    public RegistryDto sampleRegistryDto(String registryName) {
        
        RegistryDto registryDto = new RegistryDto();
        registryDto.setId(registryName);
        
        return registryDto;
    }

}
