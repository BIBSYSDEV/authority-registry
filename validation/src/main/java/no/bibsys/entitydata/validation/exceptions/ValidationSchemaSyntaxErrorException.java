package no.bibsys.entitydata.validation.exceptions;

import org.apache.jena.riot.RiotException;

public class ValidationSchemaSyntaxErrorException extends RiotException {

    private static final long serialVersionUID = -2866223016304119200L;


    public ValidationSchemaSyntaxErrorException(RiotException e) {
        super(e);
    }
}
