package no.bibsys.web.security;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class AuthenticationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        System.out.println(requestContext);
        
        String role = requestContext.getHeaderString("role");
        String registry = requestContext.getHeaderString("registry");
        
        System.out.println("Role: " + role);
        System.out.println("Registry: " + registry);
        
        requestContext
        .setSecurityContext(new AssignedSecurityContext(role, registry));
    }
}