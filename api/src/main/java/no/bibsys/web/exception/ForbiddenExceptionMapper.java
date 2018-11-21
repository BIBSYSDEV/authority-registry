package no.bibsys.web.exception;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {

	@Override
	public Response toResponse(ForbiddenException exception) {
		return Response.status(Status.FORBIDDEN).entity(exception.getMessage()).build();
	}

}