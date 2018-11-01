package no.bibsys.web;

import java.io.IOException;
import java.util.Optional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        
        Optional<String> apiKey = Optional.ofNullable(requestContext.getHeaderString(DatabaseResource.API_KEY_PARAM_NAME));
        
        if (apiKey.isPresent()) {
            System.out.println("apiKey: " + apiKey.get());
        } else {
            throw new WebApplicationException(Status.FORBIDDEN);
        }
    }

}
