package no.bibsys.web.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import no.bibsys.web.model.SimpleResponse;

@Provider
public class ConditionalCheckFailedExceptionMapper
        implements ExceptionMapper<ConditionalCheckFailedException> {

    @Override
    public Response toResponse(ConditionalCheckFailedException exception) {        
        return Response.status(Response.Status.CONFLICT).entity(new SimpleResponse("Item already exists", Status.CONFLICT)).build();
    }

}
