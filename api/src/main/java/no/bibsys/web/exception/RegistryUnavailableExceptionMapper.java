package no.bibsys.web.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

public class RegistryUnavailableExceptionMapper implements ExceptionMapper<RegistryUnavailableException> {

	@Override
	public Response toResponse(RegistryUnavailableException exception) {
		return Response.status(Status.SEE_OTHER).entity(exception.getMessage()).build();
	}

}
