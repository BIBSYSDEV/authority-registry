package no.bibsys.web.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import no.bibsys.db.exceptions.EntityNotFoundException;

@Provider
public class EntityNotFoundExceptionMapper implements ExceptionMapper<EntityNotFoundException> {

    @Override
    public Response toResponse(EntityNotFoundException exception) {
        return Response.status(Status.NOT_FOUND).entity(exception.getMessage()).build();

    }

}
