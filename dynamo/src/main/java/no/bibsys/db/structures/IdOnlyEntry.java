package no.bibsys.db.structures;

import com.fasterxml.jackson.core.JsonProcessingException;

import no.bibsys.db.ObjectMapperHelper;

public class IdOnlyEntry implements Entry {

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
    
    public String asJson() throws JsonProcessingException {
        return ObjectMapperHelper.getObjectMapper().writeValueAsString(this);
    }
}
