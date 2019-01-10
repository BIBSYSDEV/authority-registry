package no.bibsys.web.exception;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import no.bibsys.db.exceptions.RegistryMetadataTableBeingCreatedException;

public class RegistryMetadataTableBeingCreatedExceptionMapper
        implements ExceptionMapper<RegistryMetadataTableBeingCreatedException> {

    @Override
    public Response toResponse(RegistryMetadataTableBeingCreatedException exception) {
        return Response.status(Status.SERVICE_UNAVAILABLE).header(HttpHeaders.RETRY_AFTER , "20").entity(exception.getMessage()).build();
    }
}
