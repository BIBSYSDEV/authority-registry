package no.bibsys.db.structures;

public class IdOnlyEntry implements Entry {

    private String id;


    public IdOnlyEntry() {
    }

    public IdOnlyEntry(String id) {
        setId(id);
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
