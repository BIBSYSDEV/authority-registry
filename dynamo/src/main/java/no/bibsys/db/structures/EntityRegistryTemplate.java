package no.bibsys.db.structures;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.base.Preconditions;

@JsonInclude(Include.NON_NULL)
public class EntityRegistryTemplate {

    private String id;
    private Metadata metadata;
    private String schema;

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

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Override
    public String toString() {
        return "EntityRegistryTemplate [id=" + id + ", metadata=" + metadata + "]";
    }

    @JsonIgnore
    public void validate() {
        Preconditions.checkArgument(id != null && !id.isEmpty());

    }
}
