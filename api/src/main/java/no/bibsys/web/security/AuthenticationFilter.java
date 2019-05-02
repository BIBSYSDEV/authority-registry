package no.bibsys.web.security;

import java.io.IOException;
import java.util.Optional;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

import no.bibsys.service.ApiKey;
import no.bibsys.service.AuthenticationService;
import no.bibsys.web.exception.ApiKeyTableBeingCreatedException;

@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final int DELAY_IN_MILLISECONDS = 5000;
    private final transient AuthenticationService authenticationService;

    public AuthenticationFilter(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        Optional<String> apiKeyInHeader = Optional
                .ofNullable(requestContext.getHeaderString(ApiKeyConstants.API_KEY_PARAM_NAME));

        if (apiKeyInHeader.isPresent()) {
            ApiKey apiKey = null;
            int count = 5;
            do {
                try {
                    count--;
                    if(count < 0) {
                        throw new IOException("Unable to connect to resources");
                    }
                    apiKey = authenticationService.getApiKey(apiKeyInHeader.get());
                } catch (ApiKeyTableBeingCreatedException e) {
                    try {
                        Thread.sleep(DELAY_IN_MILLISECONDS);
                    } catch (InterruptedException e1) {
                        throw new IOException("Unable to connect to resources");
                    }
                }
            } while (apiKey == null);
            if (Roles.API_ADMIN.equals(apiKey.getRole())) {
                requestContext.setSecurityContext(new AssignedSecurityContext(Roles.API_ADMIN));
            } else if (Roles.REGISTRY_ADMIN.equals(apiKey.getRole())) {
                requestContext
                        .setSecurityContext(new AssignedSecurityContext(Roles.REGISTRY_ADMIN));
            }
        }
    }

}
