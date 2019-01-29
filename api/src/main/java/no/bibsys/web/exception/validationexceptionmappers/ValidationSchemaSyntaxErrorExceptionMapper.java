package no.bibsys.web.exception.validationexceptionmappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import no.bibsys.entitydata.validation.exceptions.ValidationSchemaSyntaxErrorException;

public class ValidationSchemaSyntaxErrorExceptionMapper implements
    ExceptionMapper<ValidationSchemaSyntaxErrorException> {

    @Override
    public Response toResponse(ValidationSchemaSyntaxErrorException exception) {
        return Response.status(Status.BAD_REQUEST)
            .entity("Syntax errors in SHACL validation schema").build();
    }
}
