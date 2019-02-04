package no.bibsys.utils.exception;

import org.apache.jena.riot.RiotException;

public class ValidationSchemaSyntaxErrorException extends RiotException {

    public ValidationSchemaSyntaxErrorException(RiotException e) {
        super(e);
    }
}
