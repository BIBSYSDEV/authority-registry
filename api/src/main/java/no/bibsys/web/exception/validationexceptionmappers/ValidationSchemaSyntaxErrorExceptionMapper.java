package no.bibsys.web.exception.validationexceptionmappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import no.bibsys.utils.exception.ValidationSchemaSyntaxErrorException;

public class ValidationSchemaSyntaxErrorExceptionMapper implements
    ExceptionMapper<ValidationSchemaSyntaxErrorException> {

    private static final String MESSAGE = "Syntax errors in SHACL validation schema";

    @Override
    public Response toResponse(ValidationSchemaSyntaxErrorException exception) {
        return Response.status(Status.BAD_REQUEST).entity(MESSAGE).build();
    }
}
