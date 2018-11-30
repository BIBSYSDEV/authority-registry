package no.bibsys.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import no.bibsys.db.structures.Entity;
import no.bibsys.db.structures.Registry;


public class SampleData {

    private ObjectMapper mapper = new ObjectMapper();

    
    public SampleData() {}

    public Registry sampleRegistry(String tableName) {
        Registry registry = new Registry();
        registry.setId(tableName);
        
        
        ObjectNode metadata = mapper.createObjectNode();

        registry.setMetadata(metadata);
        registry.setSchema("Schema");

        return registry;
    }
    
    public Entity sampleEntity() {
        Entity entity = new Entity();
        
        ObjectNode body = mapper.createObjectNode();

        entity.setBody(body);
        
        return entity;
    }

}
