package no.bibsys.web.model;

public class InsertEntity {

    private String message;
    private String entityId;

    public InsertEntity() {}

    public InsertEntity(String message, String entityId) {
        this.message = message;
        this.entityId = entityId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }



}
