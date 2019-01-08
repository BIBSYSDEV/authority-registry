package no.bibsys.db.exceptions;

public class SchemaTableBeingCreatedException extends Exception {

    private static final long serialVersionUID = 1796783855770289416L;

    public SchemaTableBeingCreatedException() {
        super("Resources are initializing");
    }
}
