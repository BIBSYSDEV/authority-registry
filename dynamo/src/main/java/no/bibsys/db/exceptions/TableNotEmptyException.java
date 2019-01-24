package no.bibsys.db.exceptions;

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;

public class TableNotEmptyException extends ConditionalCheckFailedException {


    /**
     * Constructs a new ConditionalCheckFailedException with the specified error message.
     *
     * @param message Describes the error encountered.
     */
    public TableNotEmptyException(String message) {
        super(message);
    }

    public TableNotEmptyException(String message, Throwable e) {
        this(String.format("%s%n%s", message, e.getMessage()));
    }
}
