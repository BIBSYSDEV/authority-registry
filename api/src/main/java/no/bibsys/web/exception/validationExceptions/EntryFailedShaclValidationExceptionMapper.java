package no.bibsys.web.exception.validationExceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import no.bibsys.entitydata.validation.exceptions.EntryFailedShaclValidationException;

public class EntryFailedShaclValidationExceptionMapper implements
    ExceptionMapper<EntryFailedShaclValidationException> {

    @Override
    public Response toResponse(EntryFailedShaclValidationException exception) {
        return Response.status(Status.BAD_REQUEST).entity("Entry failed Shacl Validations").build();
    }
}
