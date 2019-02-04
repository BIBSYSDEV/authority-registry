package no.bibsys.db.exceptions;

public class RegistryNotEmptyException  extends RuntimeException {


    public RegistryNotEmptyException(String registryName) {
        super(String.format("Registry with name %s is not empty", registryName));

    }

}
