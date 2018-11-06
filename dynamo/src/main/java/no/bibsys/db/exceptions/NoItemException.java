package no.bibsys.db.exceptions;

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;

public class NoItemException extends ConditionalCheckFailedException {

    private static final long serialVersionUID = -1917349734490906563L;
    /**
     * Constructs a new ConditionalCheckFailedException with the specified error message.
     *
     * @param message Describes the error encountered.
     */
    public NoItemException(String message) {
        super(message);
    }

    public NoItemException(String message, Throwable e) {
        this(String.format("%s%n%s", message, e.getMessage()));
    }
}
