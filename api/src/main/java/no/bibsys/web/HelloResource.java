package no.bibsys.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import io.swagger.v3.oas.annotations.Hidden;
import no.bibsys.web.model.SimpleResponse;

@Path("/hello")
public class HelloResource {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Hidden
    public SimpleResponse getHello() {
        return new SimpleResponse("Hello");
    }

}
