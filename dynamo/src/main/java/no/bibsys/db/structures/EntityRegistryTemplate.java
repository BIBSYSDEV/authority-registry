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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EntityRegistryTemplate other = (EntityRegistryTemplate) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (metadata == null) {
            if (other.metadata != null)
                return false;
        } else if (!metadata.equals(other.metadata))
            return false;
        return true;
    }
    
}
