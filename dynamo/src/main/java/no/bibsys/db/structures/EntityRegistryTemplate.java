package no.bibsys.db.structures;

public class EntityRegistryTemplate {

    private String id;
    private Metadata metadata;
    
    public EntityRegistryTemplate() {
        metadata = new Metadata();
    }
    
    public EntityRegistryTemplate(String tableName) {
        this();
        id = tableName;
        metadata.setName(tableName);
    }

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
    
    @Override
    public String toString() {
        return "EntityRegistryTemplate [id=" + id + ", metadata=" + metadata + "]";
    }
}
