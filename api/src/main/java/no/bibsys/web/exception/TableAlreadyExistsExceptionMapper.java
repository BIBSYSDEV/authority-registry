package no.bibsys.web.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import no.bibsys.web.model.SimpleResponse;

@Provider
public class TableAlreadyExistsExceptionMapper
        implements ExceptionMapper<TableAlreadyExistsException> {

    @Override
    public Response toResponse(TableAlreadyExistsException exception) {
        return Response.status(Response.Status.CONFLICT).entity(new SimpleResponse("Table already exists")).build();
    }

}
