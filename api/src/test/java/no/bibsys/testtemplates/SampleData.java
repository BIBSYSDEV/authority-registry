package no.bibsys.testtemplates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import no.bibsys.web.model.EntityDto;
import no.bibsys.web.model.RegistryDto;


public class SampleData {

    public SampleData() {}

    private final ObjectMapper mapper = new ObjectMapper();

    public EntityDto sampleEntityDto() throws JsonProcessingException {

        ObjectNode root = mapper.getNodeFactory().objectNode();
        root.put("label", "A random label");
        root.put("number", 5);
        ArrayNode array = root.putArray("myArray");
        array.add(1);
        array.add(2);
        array.add(3);


        EntityDto entityDto = new EntityDto();

        String id = "sampleId";
        entityDto.setId(id);
        entityDto.setBody(root);
        
        return entityDto;
    }

    public RegistryDto sampleRegistryDto(String registryName) {
        
        RegistryDto registryDto = new RegistryDto();
        registryDto.setId(registryName);
        
        return registryDto;
    }

}
