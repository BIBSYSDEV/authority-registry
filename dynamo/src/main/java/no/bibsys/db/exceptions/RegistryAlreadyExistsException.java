package no.bibsys.db.exceptions;

public class RegistryAlreadyExistsException extends RuntimeException {


    public RegistryAlreadyExistsException(String registryName) {
        super(String.format("A registry with name %s already exists", registryName));

    }

}
