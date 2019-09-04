package no.bibsys.web;

import no.bibsys.web.model.CustomMediaType;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Path("/json-ld/context")
public class JsonLdContextResource {

    private static final String CHARSET_UTF_8 = ";charset=utf-8";
    private static final String JSON_LD_CONTEXT_JSON = "/json_ld_context.json";

    @Path("/")
    @GET()
    @Produces(CustomMediaType.APPLICATION_JSON_LD + CHARSET_UTF_8)
    public Response getJsonLdContext() throws IOException {
        String contextObject = IOUtils.resourceToString(JSON_LD_CONTEXT_JSON, StandardCharsets.UTF_8);
        return Response.ok(contextObject).build();
    }
}
