package no.bibsys.testtemplates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
        ObjectNode langString = body.putObject("langString");
        langString.put("lang", "en");
        langString.put("value", "langStringValue");
        ArrayNode langArray = body.putArray("myLangArray");
        ObjectNode langArrayString1 = langArray.addObject();
        langArrayString1.put("lang", "en");
        langArrayString1.put("value", "langStringValue1");
        ObjectNode langArrayString2 = langArray.addObject();
        langArrayString2.put("lang", "no");
        langArrayString2.put("value", "langStringValue2");
        
        EntityDto entityDto = new EntityDto();

        String id = "sampleId";
        entityDto.setId(id);
        entityDto.setBody(mapper.writeValueAsString(body));
        
        return entityDto;
    }

    public RegistryDto sampleRegistryDto(String registryName) throws JsonProcessingException {
        
        RegistryDto registryDto = new RegistryDto();
        registryDto.setId(registryName);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode metadata = mapper.createObjectNode();
      
        metadata.put("Registry_name", "Registry name value");
        metadata.put("Registry_type", "Registry type value");
        metadata.put("Publisher", "Publisher value");
        
        registryDto.setMetadata(metadata);

        return registryDto;
    }

}
