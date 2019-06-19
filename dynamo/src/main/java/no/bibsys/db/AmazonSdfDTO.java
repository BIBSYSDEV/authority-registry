package no.bibsys.db;

import java.sql.Date;
import java.util.Arrays;

import com.fasterxml.jackson.databind.node.ObjectNode;

import no.bibsys.utils.JsonUtils;

public class AmazonSdfDTO {

    private static final String[] knownTypes = new String[] {"ADD", "REMOVE"};
    private static final String[] EVENT_NAMES = new String[] {"INSERT", "MODIFY","REMOVE"};
    
    private String type;
    private String id;
    private Date timestamp;
    private String fields;
    
    
    public AmazonSdfDTO(String eventName) {
        super();
        if (Arrays.stream(EVENT_NAMES).anyMatch(eventName::equals)) {
          if ("REMOVE".equalsIgnoreCase(eventName)) {
              this.setType("REMOVE");
          } else {
              this.setType("ADD");
          }
        } else {
            throw new IllegalArgumentException("unknown operationtype, only known operations are "+knownTypes);
        }
        
    }
    
    public String getFields() {
        return fields;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public AmazonSdfDTO setId(String id) {
        this.id = id; 
        return this;
    }
    

    public AmazonSdfDTO setTimestamp(Date timestamp) {
        this.timestamp = timestamp; 
        return this;
    }


    public void setType(String type) {
        if (Arrays.stream(knownTypes).anyMatch(type::equals)) {
            this.type = type;
        } else {
            throw new IllegalArgumentException("unknown operationtype, only known operations are "+knownTypes);
        }
    }

    public AmazonSdfDTO setBody(String body) {
        this.fields =  body; //JsonUtils.newJsonParser().createObjectNode().put("fields",body);
        return this;
    }
    
    
}
