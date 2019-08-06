package no.bibsys.db;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import no.bibsys.db.structures.Entity;
import no.bibsys.db.structures.Registry;
import no.bibsys.utils.IoUtils;
import no.bibsys.utils.JsonUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

public class SampleData {

    public static final String VALIDATION_FOLDER = "validation";
    public static final String SHACL_VALIDATION_SCHEMA_JSON = "validShaclValidationSchema.json";
    public static final String INVALID_SHACL_VALIDATION_SCHEMA_JSON = "invalidDatatypeRangeShaclValidationSchema.json";
    private final transient ObjectMapper mapper = JsonUtils.newJsonParser();
    private final transient String validValidationSchemaString;
    private final transient String invalidValidationSchemaString;

    public SampleData() throws IOException {
        validValidationSchemaString = IoUtils.resourceAsString(
            Paths.get(VALIDATION_FOLDER, SHACL_VALIDATION_SCHEMA_JSON));

        invalidValidationSchemaString = IoUtils
            .resourceAsString(Paths.get(VALIDATION_FOLDER, INVALID_SHACL_VALIDATION_SCHEMA_JSON));

    }

    public Registry sampleRegistry(String tableName) {
        Registry registry = new Registry();
        registry.setId(tableName);
        ObjectNode metadata = mapper.createObjectNode();
        metadata.put("label", "label");

        registry.setMetadata(metadata);


        return registry;
    }

    public Entity sampleEntity() throws IOException {
        Entity entity = new Entity();

        String bodyString = IoUtils.resourceAsString(Paths.get("json", "sample.json"));

        ObjectNode body = (ObjectNode) JsonUtils.newJsonParser().readTree(bodyString);

        entity.setBody(body);
        
        return entity;
    }

    
    public Entity bigsampleEntity() throws IOException {
        Entity entity = new Entity();

        String bodyString = IoUtils.resourceAsString(Paths.get("json", "bigsample2.json"));

        ObjectNode body = (ObjectNode) JsonUtils.newJsonParser().readTree(bodyString);

        entity.setBody(body);
        
        JsonNode jsonNode = body.get("@id");
        String id = jsonNode.asText().substring(jsonNode.asText().lastIndexOf("/")+1);
        entity.setId(id);
        String nowString = new Date().toString();
        entity.setCreated(nowString);
        entity.setModified(nowString);
        return entity;
    }
    
    
    
    public String getValidValidationSchemaString() {
        return validValidationSchemaString;
    }

    public String getInvalidValidationSchemaString() {
        return invalidValidationSchemaString;
    }

}
