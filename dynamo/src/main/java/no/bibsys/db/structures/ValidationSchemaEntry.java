package no.bibsys.db.structures;

public class ValidationSchemaEntry implements Entry {


    private String id;
    private String validationSchema;


    public ValidationSchemaEntry() {
    }

    ;


    public ValidationSchemaEntry(String id, String validationSchema) {
        this.id = id;
        setValidationSchema(validationSchema);
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }


    public String getValidationSchema() {
        return validationSchema;
    }

    public void setValidationSchema(String validationSchema) {
        this.validationSchema = validationSchema;
    }
}
