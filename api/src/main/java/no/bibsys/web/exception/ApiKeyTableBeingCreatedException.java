package no.bibsys.web.exception;

public class ApiKeyTableBeingCreatedException extends Exception {

    public ApiKeyTableBeingCreatedException() {
        super("Resources are initializing");
    }
    
}
