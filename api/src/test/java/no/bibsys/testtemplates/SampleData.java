package no.bibsys.testtemplates;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.bibsys.utils.IoUtils;
import no.bibsys.utils.ModelParser;
import no.bibsys.web.model.EntityDto;
import no.bibsys.web.model.RegistryDto;
import org.apache.jena.riot.Lang;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SampleData {

    public static final String VALIDATION_FOLDER = "validation";
    public static final String VALID_GRAPH_JSON = "validGraph.json";
    public static final String INVALID_GRAPH_JSON = "invalidGraph.json";

    public SampleData() {
    }

    public EntityDto sampleEntityDtoWithValidData(String expectedUri) throws IOException {
        return sampleEntityDto(VALID_GRAPH_JSON, expectedUri);
    }

    public EntityDto sampleEntityDto(String expectedUri) throws IOException {

        return sampleEntityDtoWithValidData(expectedUri);
    }

    private EntityDto sampleEntityDto(String bodyFilename, String expectedUri) throws IOException {
        String id = "sampleId";
        EntityDto entityDto = new EntityDto();
        entityDto.setId(id);
        entityDto.setCreated("2019-06-02");
        entityDto.setModified("2019-06-03");
        entityDto.setPath("http://example.org/a1234");
        String body = IoUtils.resourceAsString(Paths.get(VALIDATION_FOLDER, bodyFilename));
        body = body.replace("__REPLACE__", expectedUri);
        new ModelParser().parseModel(body, Lang.JSONLD);

        entityDto.setBody(body);
        return entityDto;
    }

    public RegistryDto sampleRegistryDto(String registryName) {

        RegistryDto registryDto = new RegistryDto();
        registryDto.setId(registryName);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> metadata = (Map<String, Object>) mapper.convertValue(mapper.createObjectNode(), Map.class);
        Map<String, String> contextMap = new HashMap<>();
        contextMap.put("@vocab", "http://example.org/entity#");
        metadata.put("@context", contextMap);

        metadata.put("Registry_name", "Registry name value");
        metadata.put("Registry_type", "Registry type value");
        metadata.put("Publisher", "Publisher value");

        registryDto.setMetadata(metadata);

        return registryDto;
    }

    public EntityDto sampleEntityDtoWithInValidData() throws IOException {
        return sampleEntityDto(INVALID_GRAPH_JSON, "http://example.org/festive");
    }
}
