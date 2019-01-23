package no.bibsys.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.nio.file.Paths;
import no.bibsys.db.structures.Entity;
import no.bibsys.db.structures.Registry;
import no.bibsys.utils.IoUtils;
import no.bibsys.utils.JsonUtils;
import org.apache.jena.rdf.model.Model;


public class SampleData {

    public static final String VALIDATION_FOLDER = "validation";
    public static final String SHACL_VALIDATION_SCHEMA_JSON = "validShaclValidationSchema.json";
    private final transient ObjectMapper mapper = JsonUtils.newJsonParser();
    private final transient String validationSchemaString;

    public SampleData() throws IOException {
        validationSchemaString = IoUtils.resourceAsString(
            Paths.get(VALIDATION_FOLDER, SHACL_VALIDATION_SCHEMA_JSON));
    }

    public Registry sampleRegistry(String tableName) {
        Registry registry = new Registry();
        registry.setId(tableName);
        ObjectNode metadata = mapper.createObjectNode();
        metadata.put("label", "label");

        registry.setMetadata(metadata);
        registry.setSchema(validationSchemaString);

        return registry;
    }
    
    public Entity sampleEntity() {
        Entity entity = new Entity();
        
        ObjectNode body = mapper.createObjectNode();

        entity.setBody(body);
        
        return entity;
    }

}
