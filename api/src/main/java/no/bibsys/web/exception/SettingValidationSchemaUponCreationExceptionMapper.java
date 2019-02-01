package no.bibsys.web.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import no.bibsys.db.exceptions.SettingValidationSchemaUponCreationException;

public class SettingValidationSchemaUponCreationExceptionMapper implements
    ExceptionMapper<SettingValidationSchemaUponCreationException> {

    private static final String MESSAGE = "You cannot set validation schema upon registry creation. Use update.";

    @Override
    public Response toResponse(SettingValidationSchemaUponCreationException exception) {
        return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).build();
    }
}
