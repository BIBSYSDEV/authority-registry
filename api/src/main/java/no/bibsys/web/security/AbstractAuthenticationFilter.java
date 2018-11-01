package no.bibsys.web.security;

import java.io.IOException;
import java.util.Optional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response.Status;
import no.bibsys.EnvironmentReader;


public abstract class AbstractAuthenticationFilter implements ContainerRequestFilter {

    public final transient String apiKey;
    
    public AbstractAuthenticationFilter(EnvironmentReader environmentReader, String apiKeyName) {
        
        apiKey = environmentReader.getEnvForName(apiKeyName).orElse("fallback-api-key");
    }
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        
        Optional<String> apiKeyInHeader = Optional.ofNullable(requestContext.getHeaderString(ApiKeyConstants.API_KEY_PARAM_NAME));
        
        if (apiKeyInHeader.isPresent() && apiKeyInHeader.get().equals(apiKey)) {
            System.out.println("apiKey in header: " + apiKeyInHeader.get());
        } else {
            throw new WebApplicationException("Provided API Key is not valid", Status.FORBIDDEN);
        }
    }

}