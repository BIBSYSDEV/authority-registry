package no.bibsys.web.exception.validationexceptionmappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import no.bibsys.entitydata.validation.exceptions.ShaclModelPathObjectsAreNotOntologyPropertiesException;

public class ShaclModelPathObjectsAreNotOntologyPropertiesExceptionMapper implements
    ExceptionMapper<ShaclModelPathObjectsAreNotOntologyPropertiesException> {

    private static final String MESSAGE = "Shacl model path objects are not ontology properties";

    @Override
    public Response toResponse(ShaclModelPathObjectsAreNotOntologyPropertiesException exception) {
        return Response.status(Status.BAD_REQUEST).entity(MESSAGE).build();
    }
}
