package no.bibsys.web;

import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import io.swagger.v3.jaxrs2.integration.resources.BaseOpenApiResource;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;

@Path("/openapi")
public class CustomOpenApiResource extends BaseOpenApiResource {
  
    @Context
    private transient ServletConfig config;

    @Context
    private transient Application app;

    @GET
    @Path("/")
    @Hidden
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(hidden = true)
    public Response getOpenApi(@Context HttpHeaders headers,
                               @Context UriInfo uriInfo) {

        try {
            return super.getOpenApi(headers, config, app, uriInfo, "yaml");
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }
}