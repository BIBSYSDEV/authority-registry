package no.bibsys.db;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.internal.LinkedTreeMap;

public class AmazonSdfDTO {

    public static final String CLOUDSEARCH_PRESENTATION_FIELD = "presentation_json";
    public static final String CLOUDSEARCH_MODIFIED_TIMESTAMP_FIELD = "modified";
    public static final String  CLOUDSEARCH_OVERFLOW_FIELD = "overflow"; // Any property not mapped goes here

    public enum CloudsearchOperation {
        ADD, DELETE
    }
    
    public enum EventName {
        INSERT(CloudsearchOperation.ADD), 
        MODIFY(CloudsearchOperation.ADD), 
        REMOVE(CloudsearchOperation.DELETE);
        
        public final CloudsearchOperation cloudsearchOperation;
        
        EventName(CloudsearchOperation cloudsearchSdfType) {
            this.cloudsearchOperation = cloudsearchSdfType;
        }
    }
    
    public enum SearchFieldsNameMap {
        alternativelabel("alternativeLabel","alternativelabel"), 
        broader("broader","broader"), 
        definition("definition","definition"),
        identifier("id","identifier"),
        inscheme("inscheme","inscheme"),
        localidentifier("localIdentifier","localidentifier"),
        modified("modified","modified"),
        narrower("narrower","narrower"),
        preferredlabel("preferredLabel", "preferredlabel"),
        related("related","related"),
        seealso("seeAlso","seealso"),
        cloudsearch_presentation_field(CLOUDSEARCH_PRESENTATION_FIELD, CLOUDSEARCH_PRESENTATION_FIELD)
        ;
        
        public final String sourceField;
        public final String searchField;
        
        SearchFieldsNameMap(String sourceField, String searchField) {
            this.sourceField = sourceField;
            this.searchField = searchField;
        }
    }
    
    private final String type;
    private transient String id;
    private final transient LinkedTreeMap<String, Object> fields = new LinkedTreeMap<>();
    private final transient Map<String, String> mappings = new ConcurrentHashMap<>();

    
    public AmazonSdfDTO(String eventName) {
        type =  EventName.valueOf(eventName).cloudsearchOperation.name().toLowerCase(Locale.getDefault());
        initMapping();
    }

    public AmazonSdfDTO(CloudsearchOperation cloudsearchOperation, String entityIdentifier) {
        this.type = cloudsearchOperation.name().toLowerCase(Locale.getDefault());
        this.id = entityIdentifier;
        initMapping();
    }
    
    private void initMapping() {
        for (SearchFieldsNameMap fieldMap : SearchFieldsNameMap.values()) {
            mappings.put(fieldMap.sourceField, fieldMap.searchField);
        }
    }

    @SuppressWarnings("PMD")
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(60);
        str.append("AmazonSdfDTO [type=").append(type).append(", id=").append(id).append(", fields={");
        fields.forEach((key, value) -> str.append(key).append("=").append(value).append(", "));
        str.append("}]");
        return str.toString();
    }

    public Map<String, ?> getFields() {
        return fields;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setField(String fieldName, Object value) {
        fields.put(getSearchFieldName(fieldName), value);
    }

    private String getSearchFieldName(String sourceField) {
        if (mappings.containsKey(sourceField)) {
            return mappings.get(sourceField);              
        } else {
            return CLOUDSEARCH_OVERFLOW_FIELD;
        }
    }
}
