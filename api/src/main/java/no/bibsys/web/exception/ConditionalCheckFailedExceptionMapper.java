package no.bibsys.web.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;

@Provider
public class ConditionalCheckFailedExceptionMapper
        implements ExceptionMapper<ConditionalCheckFailedException> {

    @Override
    public Response toResponse(ConditionalCheckFailedException exception) {        
        return Response.status(Response.Status.CONFLICT).entity("Item already exists").build();
    }

}
