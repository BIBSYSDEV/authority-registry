package no.bibsys.db.exceptions;

public class RegistryNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 771312714770513329L;

    public RegistryNotFoundException(String registryName) {
        super(String.format("Registry with name %s does not exist", registryName));
    }

    public RegistryNotFoundException(String registryName, String validationSchemaTableName) {
        super(String.format("Schema for registry with name %s does not exist in schema table %s",
                registryName, validationSchemaTableName));
    }

}
