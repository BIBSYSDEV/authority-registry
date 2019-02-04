package no.bibsys.testtemplates;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.jena.riot.Lang;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.bibsys.utils.IoUtils;
import no.bibsys.utils.ModelParser;
import no.bibsys.web.model.EntityDto;
import no.bibsys.web.model.RegistryDto;


public class SampleData {

    public static final String VALIDATION_FOLDER = "validation";
    public static final String VALID_GRAPH_JSON = "validGraph.json";
    public static final String INVALID_GRAPH_JSON = "invalidGraph.json";

    public SampleData() {
    }


    public EntityDto sampleEntityDtoWithValidData() throws IOException {
        return sampleEntityDto(VALID_GRAPH_JSON);
    }

    public EntityDto sampleEntityDto() throws IOException {

        return sampleEntityDtoWithValidData();
    }

    private EntityDto sampleEntityDto(String bodyFilename) throws IOException {
        String id = "sampleId";
        EntityDto entityDto = new EntityDto();
        entityDto.setId(id);
        String body = IoUtils.resourceAsString(Paths.get(VALIDATION_FOLDER, bodyFilename));
        new ModelParser().parseModel(body, Lang.JSONLD);

        entityDto.setBody(body);
        return entityDto;

    }

    public EntityDto sampleEntityDtoWithInValidData() throws IOException {
        return sampleEntityDto(INVALID_GRAPH_JSON);
    }


    public RegistryDto sampleRegistryDto(String registryName) throws IOException {

        RegistryDto registryDto = new RegistryDto();
        registryDto.setId(registryName);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> metadata = (Map<String, Object>) mapper
            .convertValue(mapper.createObjectNode(), Map.class);

        metadata.put("Registry_name", "Registry name value");
        metadata.put("Registry_type", "Registry type value");
        metadata.put("Publisher", "Publisher value");

        registryDto.setMetadata(metadata);


        return registryDto;
    }

}
