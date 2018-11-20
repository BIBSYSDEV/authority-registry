package no.bibsys.web.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

public class RegistryAlreadyExistsExceptionMapper implements ExceptionMapper<RegistryAlreadyExistsException> {

	@Override
	public Response toResponse(RegistryAlreadyExistsException exception) {
		return Response.status(Status.CONFLICT).entity(exception.getMessage()).build();
	}

}
