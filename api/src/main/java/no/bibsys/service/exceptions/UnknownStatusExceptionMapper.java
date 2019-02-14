package no.bibsys.service.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UnknownStatusExceptionMapper
        implements ExceptionMapper<UnknownStatusException> {

    private static final String REGISTRY_IN_UNKNOWN_STATUS = "Registry in unknown status.";

    @Override
    public Response toResponse(UnknownStatusException exception) {
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(REGISTRY_IN_UNKNOWN_STATUS).build();
    }

}
