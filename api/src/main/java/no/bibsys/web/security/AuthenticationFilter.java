package no.bibsys.web.security;

import java.io.IOException;
import java.util.Optional;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import no.bibsys.EnvironmentReader;

@Provider
@PreMatching
public class AuthenticationFilter implements ContainerRequestFilter {
    
    private final transient EnvironmentReader environmentReader;
    
    public AuthenticationFilter(EnvironmentReader environmentReader) {
        this.environmentReader = environmentReader;
    }
    
    public boolean isOneOfApiAdminApiKeys(String apiKeyinHeader) {
        String apiKey = environmentReader.getEnvForName(ApiKeyConstants.API_ADMIN_API_KEY).orElse("fallback-api-admin-api-key");
        return apiKeyinHeader.equals(apiKey);
    }
    
    public boolean isOneOfRegistryAdminApiKeys(String apiKeyInHeader) {
        String apiKey = environmentReader.getEnvForName(ApiKeyConstants.REGISTRY_ADMIN_API_KEY).orElse("fallback-registry-admin-api-key");
        return apiKeyInHeader.equals(apiKey);
    }
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {   
        
        Optional<String> apiKeyInHeader = Optional.ofNullable(requestContext.getHeaderString(ApiKeyConstants.API_KEY_PARAM_NAME));
        
        if (apiKeyInHeader.isPresent() && isOneOfApiAdminApiKeys(apiKeyInHeader.get())) {
            requestContext.setSecurityContext(new AssignedSecurityContext(Roles.API_ADMIN));
        } else if (apiKeyInHeader.isPresent() && isOneOfRegistryAdminApiKeys(apiKeyInHeader.get())) {
            requestContext.setSecurityContext(new AssignedSecurityContext(Roles.REGISTRY_ADMIN));
        }
    }

}