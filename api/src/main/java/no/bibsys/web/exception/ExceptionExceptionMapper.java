package no.bibsys.web.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import no.bibsys.web.model.SimpleResponse;

@Provider
public class ExceptionExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new SimpleResponse(exception.getMessage(), Status.INTERNAL_SERVER_ERROR)).build();
    }

}
