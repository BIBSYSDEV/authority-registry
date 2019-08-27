package no.bibsys.db;

import java.util.Locale;
import java.util.Map;

import com.google.gson.internal.LinkedTreeMap;

public class AmazonSdfDTO {

    public static final String CLOUDSEARCH_PRESENTAION_FIELD = "presentation_json";
    public static final String CLOUDSEARCH_MODIFIED_TIMESTAMP_FIELD = "modified";

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

    private final String type;
    private transient String id;
    private final transient LinkedTreeMap<String, Object> fields = new LinkedTreeMap<>();

    public AmazonSdfDTO(String eventName) {
        type =  EventName.valueOf(eventName).cloudsearchOperation.name().toLowerCase(Locale.getDefault());
    }

    public AmazonSdfDTO(CloudsearchOperation cloudsearchOperation) {
        this.type = cloudsearchOperation.name().toLowerCase(Locale.getDefault());
    }

    public AmazonSdfDTO(CloudsearchOperation cloudsearchOperation, String entityIdentifier) {
        this.type = cloudsearchOperation.name().toLowerCase(Locale.getDefault());
        this.id = entityIdentifier;
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
        fields.put(fieldName, value);
    }
    
}
