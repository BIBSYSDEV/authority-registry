package no.bibsys.web.exception.validationexceptionmappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import no.bibsys.entitydata.validation.exceptions.EntityFailedShaclValidationException;

public class EntityFailedShaclValidationExceptionMapper implements
    ExceptionMapper<EntityFailedShaclValidationException> {

    private static final String MESSAGE = "Entry failed Shacl Validations";

    @Override
    public Response toResponse(EntityFailedShaclValidationException exception) {
        return Response.status(Status.BAD_REQUEST).entity(MESSAGE).build();
    }
}
