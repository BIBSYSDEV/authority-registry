package no.bibsys.web.exception.validationexceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import no.bibsys.entitydata.validation.exceptions.ShaclModelTargetClassesAreNotClassesOfOntologyException;

public class ShaclModelTargetClassesAreNotClassesOfOntologyExceptionMapper implements
    ExceptionMapper<ShaclModelTargetClassesAreNotClassesOfOntologyException> {

    @Override
    public Response toResponse(ShaclModelTargetClassesAreNotClassesOfOntologyException exception) {
        return Response.status(Status.BAD_REQUEST)
            .entity("Shacl model target classes are not classes of ontology").build();
    }
}
