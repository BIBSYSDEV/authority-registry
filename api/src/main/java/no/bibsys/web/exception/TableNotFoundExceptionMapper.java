package no.bibsys.web.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import com.amazonaws.services.dynamodbv2.model.TableNotFoundException;
import no.bibsys.web.model.SimpleResponse;

@Provider
public class TableNotFoundExceptionMapper implements ExceptionMapper<TableNotFoundException> {

    @Override
    public Response toResponse(TableNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND).entity(new SimpleResponse(exception.getMessage())).build();
    }

}
