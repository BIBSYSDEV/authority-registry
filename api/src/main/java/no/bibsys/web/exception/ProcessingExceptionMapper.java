package no.bibsys.web.exception;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ProcessingExceptionMapper implements ExceptionMapper<ProcessingException> {

    private static final String MESSAGE = "Error processing one of the fields";

    @Override
    public Response toResponse(ProcessingException exception) {
        return Response.status(Response.Status.BAD_REQUEST).entity(MESSAGE)
            .build();
    }


}
