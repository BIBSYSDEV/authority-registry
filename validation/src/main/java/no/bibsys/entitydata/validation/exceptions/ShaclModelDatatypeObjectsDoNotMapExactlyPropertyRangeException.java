package no.bibsys.entitydata.validation.exceptions;

public class ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeException extends ShaclModelValidationException {

    private static final String MESSAGE_TEMPLATE = "The ShaCL model provided attempts to match " +
            "non-ontology property ranges: %n%n %s";
    public ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeException(String messageBody) {
        super(String.format(MESSAGE_TEMPLATE, messageBody));
    }
}
