package no.bibsys.web.exception.validationexceptionmappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import no.bibsys.entitydata.validation.exceptions.ShaclModelTargetClassesAreNotInDomainOfRespectivePropertiesException;

public class ShaclModelTargetClassesAreNotInDomainOfRespectivePropertiesExceptionMapper implements 
    ExceptionMapper<ShaclModelTargetClassesAreNotInDomainOfRespectivePropertiesException> {

    private static final String MESSAGE = "Shacl model target classes are not in domain of respective properties";

    @Override
    public Response toResponse(
            ShaclModelTargetClassesAreNotInDomainOfRespectivePropertiesException exception) {
        return Response.status(Status.BAD_REQUEST).entity(MESSAGE)
                .build();
    }
}
