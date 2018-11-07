package no.bibsys.web.exception;

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;

public class RegistryNotFoundException extends ConditionalCheckFailedException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ConditionalCheckFailedException with the specified error message.
     *
     * @param message Describes the error encountered.
     */
    public RegistryNotFoundException(String message) {
        super(message);
    }

    public RegistryNotFoundException(String message, Throwable e) {
        this(String.format("%s%n%s", message, e.getMessage()));
    }
}
