package no.bibsys.web.exception;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

public class ApiKeyTableBeingCreatedExceptionMapper
        implements ExceptionMapper<ApiKeyTableBeingCreatedException> {

    private static final String SECONDS_FOR_WAIT = "20";

    @Override
    public Response toResponse(ApiKeyTableBeingCreatedException exception) {
        return Response.status(Status.SERVICE_UNAVAILABLE).header(HttpHeaders.RETRY_AFTER, SECONDS_FOR_WAIT)
            .entity(exception.getMessage()).build();
    }
}
