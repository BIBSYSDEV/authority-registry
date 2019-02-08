package no.bibsys.db.exceptions;

public class RegistryCreationFailureException extends Exception {

    public RegistryCreationFailureException(String id) {
        super(id);
    }
}
