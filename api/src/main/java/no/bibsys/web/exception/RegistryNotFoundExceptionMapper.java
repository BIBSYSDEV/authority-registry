package no.bibsys.web.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import no.bibsys.web.model.SimpleResponse;

@Provider
public class RegistryNotFoundExceptionMapper implements ExceptionMapper<RegistryNotFoundException> {

    @Override
    public Response toResponse(RegistryNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new SimpleResponse("Table does not exist"))
                .build();
    }
}
