package no.bibsys.web.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import no.bibsys.web.model.SimpleResponse;

@Provider
public class RegistryAlreadyExistsExceptionMapper implements ExceptionMapper<RegistryAlreadyExistsException> {

    @Override
    public Response toResponse(RegistryAlreadyExistsException exception) {
        return Response.status(Response.Status.CONFLICT).entity(new SimpleResponse("Table already exists")).build();
    }

}
