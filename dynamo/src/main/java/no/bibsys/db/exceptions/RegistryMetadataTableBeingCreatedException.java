package no.bibsys.db.exceptions;

public class RegistryMetadataTableBeingCreatedException extends Exception {


    public RegistryMetadataTableBeingCreatedException() {
        super("Resources are initializing");
    }
}
