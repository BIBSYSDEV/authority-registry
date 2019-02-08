package no.bibsys.db.exceptions;

public class RegistryCreationFailureException extends Exception {

    private final static String MESSAGE = "Failed to create Registry with name:%s";

    public RegistryCreationFailureException(String registryId){
        super(String.format(MESSAGE,registryId));
    }

}
