package no.bibsys.web.exception.validationexceptionmappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import no.bibsys.entitydata.validation.exceptions.ShaclModelPropertiesAreNotIcludedInOntologyException;

public class ShaclModelPropertiesAreNotIcludedInOntologyExceptionMapper implements
    ExceptionMapper<ShaclModelPropertiesAreNotIcludedInOntologyException> {

    private static final String MESSAGE = "Shacl model properties are not included in ontology";

    @Override
    public Response toResponse(ShaclModelPropertiesAreNotIcludedInOntologyException exception) {
        return Response.status(Status.BAD_REQUEST).entity(MESSAGE).build();
    }
}
