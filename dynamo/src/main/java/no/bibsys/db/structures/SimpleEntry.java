package no.bibsys.db.structures;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import no.bibsys.db.ObjectMapperHelper;

public class SimpleEntry implements Entry {


    private String id;
    private List<LanguageString> preferredLabels;


    public SimpleEntry() {
    }

    public SimpleEntry(final String id, final List<LanguageString> preferredLabels) {
        this.id = id;
        this.preferredLabels = preferredLabels;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(final String id) {
        this.id = id;
    }


    public List<LanguageString> getPreferredLabels() {
        return this.preferredLabels;
    }


    public void setPreferredLabels(final List<LanguageString> labels) {
        this.preferredLabels = labels;
    }

    public String asJson() throws JsonProcessingException {
        return ObjectMapperHelper.getObjectMapper().writeValueAsString(this);
    }
}
