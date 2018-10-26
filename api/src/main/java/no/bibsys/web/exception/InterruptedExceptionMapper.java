package no.bibsys.web.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import no.bibsys.web.model.SimpleResponse;

@Provider
public class InterruptedExceptionMapper implements ExceptionMapper<InterruptedException> {

    @Override
    public Response toResponse(InterruptedException exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new SimpleResponse(exception.getMessage())).build();
    }

}