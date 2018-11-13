package no.bibsys.db.structures;

public class ValidationSchemaEntry implements DynamoDbEntry {


    private String id;
    private String validationSchema;


    public ValidationSchemaEntry() { }


    public ValidationSchemaEntry(String id, String validationSchema) {
        this.id = id;
        setValidationSchema(validationSchema);
    }


    @Override
    public final String getId() {
        return id;
    }

    @Override
    public final  void setId(String id) {
        this.id = id;
    }


    public final String getValidationSchema() {
        return validationSchema;
    }

    public final void  setValidationSchema(String validationSchema) {
        this.validationSchema = validationSchema;
    }
}
