package no.bibsys.web.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RegistryNotFoundExceptionMapper implements ExceptionMapper<RegistryNotFoundException> {

	@Override
	public Response toResponse(RegistryNotFoundException exception) {
		return Response.status(Status.NOT_FOUND).entity(exception.getMessage()).build();
	}

}
