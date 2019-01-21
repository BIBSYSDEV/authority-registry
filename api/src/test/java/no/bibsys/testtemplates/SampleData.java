package no.bibsys.testtemplates;

import java.util.Map;

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
        body.put("@id","http://example.org/fakevoc/c00000");
        body.put("@type", "bsa:Concept");
        
        ObjectNode alternativeLabel = body.putObject("alternativeLabel");
        alternativeLabel.put("@language", "no");
        alternativeLabel.put("@value", "Animalia");
        body.put("inScheme", "http://example.org/fakevoc");
        ArrayNode narrowerArray = body.putArray("narrower");
        narrowerArray.add("http://example.org/fakevoc/c00003");
        narrowerArray.add("http://example.org/fakevoc/c00001");
        ArrayNode prefLabel = body.putArray("preferredLabel");
        ObjectNode pref1 = prefLabel.addObject();
        pref1.put("@language", "no");
        pref1.put("@value", "Dyr");
        ObjectNode pref2 = prefLabel.addObject();
        pref2.put("@language", "en");
        pref2.put("@value", "Animals");
        
//        body.put("label", "A random label");
//        body.put("number", 5);
//        ArrayNode array = body.putArray("myArray");
//        array.add(1);
//        array.add(2);
//        array.add(3);
//        ObjectNode langString = body.putObject("langString");
//        langString.put("@language", "en");
//        langString.put("@value", "langStringValue");
//        ArrayNode langArray = body.putArray("myLangArray");
//        ObjectNode langArrayString1 = langArray.addObject();
//        langArrayString1.put("@language", "en");
//        langArrayString1.put("@value", "langStringValue1");
//        ObjectNode langArrayString2 = langArray.addObject();
//        langArrayString2.put("@language", "no");
//        langArrayString2.put("@value", "langStringValue2");
        
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
        Map<String,Object> metadata = (Map<String, Object>)mapper.convertValue(
            mapper.createObjectNode(), Map.class);
      
        metadata.put("Registry_name", "Registry name value");
        metadata.put("Registry_type", "Registry type value");
        metadata.put("Publisher", "Publisher value");
        
        registryDto.setMetadata(metadata);

        return registryDto;
    }

}
