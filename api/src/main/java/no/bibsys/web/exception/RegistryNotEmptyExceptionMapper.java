package no.bibsys.web.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RegistryNotEmptyExceptionMapper implements ExceptionMapper<RegistryNotEmptyException> {

    @Override
    public Response toResponse(RegistryNotEmptyException exception) {
        return Response.status(Status.METHOD_NOT_ALLOWED).entity(exception.getMessage()).build();
    }

}
