package no.bibsys.web.exception.validationExceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import no.bibsys.entitydata.validation.exceptions.ShaclModelPathObjectsAreNotOntologyPropertiesException;

public class ShaclModelPathObjectsAreNotOntologyPropertiesExceptionMapper implements
    ExceptionMapper<ShaclModelPathObjectsAreNotOntologyPropertiesException> {

    @Override
    public Response toResponse(ShaclModelPathObjectsAreNotOntologyPropertiesException exception) {
        return Response.status(Status.BAD_REQUEST)
            .entity("Shacl model path objects are not ontology properties").build();
    }
}
