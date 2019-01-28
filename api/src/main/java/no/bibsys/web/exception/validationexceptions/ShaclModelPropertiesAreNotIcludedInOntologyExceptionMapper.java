package no.bibsys.web.exception.validationexceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import no.bibsys.entitydata.validation.exceptions.ShaclModelPropertiesAreNotIcludedInOntologyException;

public class ShaclModelPropertiesAreNotIcludedInOntologyExceptionMapper implements
    ExceptionMapper<ShaclModelPropertiesAreNotIcludedInOntologyException> {

    @Override
    public Response toResponse(ShaclModelPropertiesAreNotIcludedInOntologyException exception) {
        return Response.status(Status.BAD_REQUEST)
            .entity("Shacl model properties are not included in ontology").build();
    }
}
