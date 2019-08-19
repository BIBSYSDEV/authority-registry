package no.bibsys.entitydata.validation.exceptions;

public class EntityFailedShaclValidationException extends Exception {
    private static final String ERROR_MESSAGE_TEMPLATE = "The entity failed validation with the report: %n%n%s";

    public EntityFailedShaclValidationException(String messageBody) {
        super(String.format(ERROR_MESSAGE_TEMPLATE, messageBody));
    }
}
