package no.bibsys.db;

public class TestTemplate {

    private String id;
    private Metadata metadata;

    public TestTemplate(String tableName) {
        id = tableName;
        metadata = new Metadata();
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
        return "TestTemplate [id=" + id + ", metadata=" + metadata + "]";
    }
}
