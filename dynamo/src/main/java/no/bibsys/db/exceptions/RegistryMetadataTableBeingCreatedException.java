package no.bibsys.db.exceptions;

public class RegistryMetadataTableBeingCreatedException extends Exception {

    private static final long serialVersionUID = 1796783855770289416L;

    public RegistryMetadataTableBeingCreatedException() {
        super("Resources are initializing");
    }
}
