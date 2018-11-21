package no.bibsys.web.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import no.bibsys.db.exceptions.RegistryNotFoundException;

public class RegistryNotFoundExceptionMapper implements ExceptionMapper<RegistryNotFoundException> {

	@Override
	public Response toResponse(RegistryNotFoundException exception) {
		return Response.status(Status.NOT_FOUND).entity(exception.getMessage()).build();
	}

}
