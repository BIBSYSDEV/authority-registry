package no.bibsys.web.model;

public class CreateRegistryRequest extends EditRegistryRequest {

    private String validationSchema;

    public CreateRegistryRequest() {
        super(EditRegistryRequest.CREATE);
    }


    public CreateRegistryRequest(String registryName) {
        super(EditRegistryRequest.CREATE, registryName);
    }


    public CreateRegistryRequest(String registryName, String validationSchema) {
        super(EditRegistryRequest.CREATE, registryName);
        this.validationSchema = validationSchema;
    }


    public String getValidationSchema() {
        return validationSchema;
    }

    public void setValidationSchema(String validationSchema) {
        this.validationSchema = validationSchema;
    }


}
