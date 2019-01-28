package no.bibsys.web.exception.validationexceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import no.bibsys.entitydata.validation.exceptions.ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeException;

public class ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeExceptionMapper implements
    ExceptionMapper<ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeException> {

    public static final String MESSAGE = "Shacl model datatype objects do not map exactly "
        + "property ranges";

    @Override
    public Response toResponse(
        ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeException exception) {
        return Response.status(Status.BAD_REQUEST).entity(MESSAGE).build();
    }
}
