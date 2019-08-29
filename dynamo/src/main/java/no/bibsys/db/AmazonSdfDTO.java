package no.bibsys.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.gson.internal.LinkedTreeMap;


public class AmazonSdfDTO {

    private static final String JSON_LD_VALUE = "value";
    public static final String CLOUDSEARCH_PRESENTATION_FIELD = "presentation_json";
    public static final String CLOUDSEARCH_MODIFIED_TIMESTAMP_FIELD = "modified";
    public static final String  CLOUDSEARCH_OVERFLOW_FIELD = "overflow"; // Any property not mapped goes here
    public static final String  NO_CLOUDSEARCH_MAPPING = "DONT-PUT-THIS-IN-CLOUDSEARCH"; //  Not inserting i CS

    private static final Logger logger = LoggerFactory.getLogger(AmazonSdfDTO.class);

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
        inscheme("inScheme","inscheme"),
        localidentifier("localIdentifier","localidentifier"),
        modified("modified","modified"),
        narrower("narrower","narrower"),
        preferredlabel("preferredLabel", "preferredlabel"),
        related("related","related"),
        seealso("seeAlso","seealso"),
        sameas("sameAs","sameas"),
        type("type","type"),
        context("@context", NO_CLOUDSEARCH_MAPPING),
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

    public void setField(String fieldName, JsonNode value) {
        try {
            String targetSearchFieldName = getSearchFieldName(fieldName);
            if (!NO_CLOUDSEARCH_MAPPING.equalsIgnoreCase(targetSearchFieldName)) {
                Object extractedValue = extractFieldValue(value);
                fields.put(targetSearchFieldName, extractedValue);
            }
        } catch (Exception e) {
            logger.debug("fieldName={}",fieldName,e);
        }
    }

    public void setField(String fieldName, String value) {
        String targetSearchFieldName = getSearchFieldName(fieldName);
        if (!NO_CLOUDSEARCH_MAPPING.equalsIgnoreCase(targetSearchFieldName)) {
            fields.put(targetSearchFieldName, value);
        }
    }





    private Object extractFieldValue(JsonNode value) {
        switch (value.getNodeType()) { 
        case ARRAY:
            JsonNodeType elementType = value.get(0).getNodeType();
            if (elementType == JsonNodeType.OBJECT) {
                return getValueFieldsFromArrayOfObjects((ArrayNode) value);
            } else {
                return Arrays.asList(value);
            }
        case OBJECT:
            return getJsonObjectValueField(value);
        case STRING:
            return value.asText();
        default:
            return value.asText();
        }
    }

    private String getJsonObjectValueField(JsonNode value) {
        return value.findValue(JSON_LD_VALUE).asText();
    }

    private String[] getValueFieldsFromArrayOfObjects(ArrayNode array) {
        List<String> stringList = new ArrayList<>();
        array.forEach(element -> stringList.add(getJsonObjectValueField(element)));
        if (stringList.isEmpty()) {
            return new String[0];  
        } 
        String[] stringArr = new String[stringList.size()]; 
        return (String[]) stringList.toArray(stringArr);
    }

    private String getSearchFieldName(String sourceField) {
        if (mappings.containsKey(sourceField)) {
            return mappings.get(sourceField);              
        } else {
            logger.debug("No mapping for field={}, using {}",sourceField, CLOUDSEARCH_OVERFLOW_FIELD);
            return CLOUDSEARCH_OVERFLOW_FIELD;
        }
    }
}
