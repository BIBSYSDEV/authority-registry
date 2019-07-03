package no.bibsys.web;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class EntityExceptionMapper implements ExceptionMapper<Exception> {

    Logger logger = LoggerFactory.getLogger(EntityExceptionMapper.class);

    public Response toResponse(Exception ex) {

        System.out.println("EntityExceptionMapper: " + ex.getMessage());
        logger.debug(ex.getMessage(), ex);

        return Response.status(501).entity(ex.getMessage()).type("text/plain").build();
    }
}
