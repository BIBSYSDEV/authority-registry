package no.bibsys.web.exception.validationexceptionmappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import no.bibsys.entitydata.validation.exceptions.ShaclModelTargetClassesAreNotClassesOfOntologyException;

public class ShaclModelTargetClassesAreNotClassesOfOntologyExceptionMapper implements
    ExceptionMapper<ShaclModelTargetClassesAreNotClassesOfOntologyException> {

    private static final String MESSAGE = "Shacl model target classes are not classes of ontology";

    @Override
    public Response toResponse(ShaclModelTargetClassesAreNotClassesOfOntologyException exception) {
        return Response.status(Status.BAD_REQUEST).entity(MESSAGE).build();
    }
}
