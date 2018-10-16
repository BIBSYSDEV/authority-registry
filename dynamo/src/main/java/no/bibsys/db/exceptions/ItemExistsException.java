package no.bibsys.db.exceptions;

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import java.util.UUID;

public class ItemExistsException extends ConditionalCheckFailedException {

    private final UUID serialVersionUID = UUID.randomUUID();

    /**
     * Constructs a new ConditionalCheckFailedException with the specified error message.
     *
     * @param message Describes the error encountered.
     */
    public ItemExistsException(String message) {
        super(message);
    }

    public ItemExistsException(String message, Throwable e) {
        this(String.format("%s%n%s", message, e.getMessage()));
    }
}
