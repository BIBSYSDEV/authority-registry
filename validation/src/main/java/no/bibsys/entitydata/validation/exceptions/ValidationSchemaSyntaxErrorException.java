package no.bibsys.entitydata.validation.exceptions;

import org.apache.jena.riot.RiotException;

public class ValidationSchemaSyntaxErrorException extends RiotException {

    public ValidationSchemaSyntaxErrorException(RiotException e) {
        super(e);
    }
}
