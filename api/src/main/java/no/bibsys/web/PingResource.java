package no.bibsys.web;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.bibsys.web.security.ApiKeyConstants;

@Path("/ping")
public class PingResource {

    private static final Logger logger = LoggerFactory.getLogger(PingResource.class);

    @GET
    @Path("/")
    public Response ping(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey) {
        if (apiKey == null) {
            System.out.println("APIKEY is null");
            logger.info("APIKEY is null");
        }
        System.out.println(apiKey);
        logger.info("APIKEY:" + apiKey);
        return Response.ok().build();
    }
}
