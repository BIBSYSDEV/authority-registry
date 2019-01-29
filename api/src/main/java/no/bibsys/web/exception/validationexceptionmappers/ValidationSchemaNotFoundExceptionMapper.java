package no.bibsys.web.exception.validationexceptionmappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import no.bibsys.service.exceptions.ValidationSchemaNotFoundException;

public class ValidationSchemaNotFoundExceptionMapper implements ExceptionMapper<ValidationSchemaNotFoundException> {

    public static final String MESSAGE = "Validation schema not found";

    @Override
    public Response toResponse(ValidationSchemaNotFoundException exception) {
        return Response.status(Status.FORBIDDEN).entity(MESSAGE).build();
    }
}
