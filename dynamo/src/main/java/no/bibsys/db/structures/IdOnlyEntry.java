package no.bibsys.db.structures;

public class IdOnlyEntry implements DynamoDbEntry {

    private String id;


    public IdOnlyEntry() {
    }

    public IdOnlyEntry(String id) {
        setId(id);
    }


    @Override
    public final  String getId() {
        return id;
    }

    @Override
    public final void setId(String id) {
        this.id = id;
    }


}
