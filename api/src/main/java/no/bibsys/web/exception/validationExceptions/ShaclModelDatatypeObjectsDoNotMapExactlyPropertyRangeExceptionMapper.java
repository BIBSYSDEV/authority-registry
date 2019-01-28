package no.bibsys.web.exception.validationExceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import no.bibsys.entitydata.validation.exceptions.ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeException;

public class ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeExceptionMapper implements
    ExceptionMapper<ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeException> {

    @Override
    public Response toResponse(
        ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeException exception) {
        return Response.status(Status.BAD_REQUEST)
            .entity("Shacl model datatype Objects do not map exactly property ranges").build();
    }
}
