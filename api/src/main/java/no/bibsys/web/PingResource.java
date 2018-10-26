package no.bibsys.web;

import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/ping")
public class PingResource {

    @HEAD
    @Path("/")
    public Response ping() {
        return Response.ok().build();
    }
    
}
