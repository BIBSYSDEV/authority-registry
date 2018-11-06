package no.bibsys.web.security;

import java.io.IOException;
import java.util.Optional;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import no.bibsys.service.ApiKey;
import no.bibsys.service.AuthenticationService;

@Provider
@PreMatching
public class AuthenticationFilter implements ContainerRequestFilter {
    
    private final transient AuthenticationService authenticationService;
    
    public AuthenticationFilter(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {   
        
        Optional<String> apiKeyInHeader = Optional.ofNullable(requestContext.getHeaderString(ApiKeyConstants.API_KEY_PARAM_NAME));
        
        if (apiKeyInHeader.isPresent()) {
            ApiKey apiKey = authenticationService.getApiKey(apiKeyInHeader.get());
            if (apiKey.isActive()) {
                if (apiKey.getRoles().contains(Roles.API_ADMIN)) {
                    requestContext.setSecurityContext(new AssignedSecurityContext(Roles.API_ADMIN));
                } else if (apiKey.getRoles().contains(Roles.REGISTRY_ADMIN)) {
                    requestContext.setSecurityContext(new AssignedSecurityContext(Roles.REGISTRY_ADMIN));
                }
            }
        }
    }

}