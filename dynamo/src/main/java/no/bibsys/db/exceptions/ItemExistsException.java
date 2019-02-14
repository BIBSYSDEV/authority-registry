package no.bibsys.db.exceptions;

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;

public class ItemExistsException extends ConditionalCheckFailedException {


    /**
     * Constructs a new ConditionalCheckFailedException with the specified error message.
     *
     * @param message Describes the error encountered.
     */
    public ItemExistsException(String message) {
        super(message);
    }

}
