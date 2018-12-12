package no.bibsys.web.security;

import java.security.Principal;
import javax.ws.rs.core.SecurityContext;

public class AssignedSecurityContext implements SecurityContext {

    private final transient String assignedRole;
    private final transient String registry;
    
    public AssignedSecurityContext(String asssignedRole, String registry) {
        this.assignedRole = asssignedRole;
        this.registry = registry;
    }

    @Override
    public Principal getUserPrincipal() {
        return new Principal() {
            @Override
            public String getName() {
                return "Jersey";
            }
        };
    }

    @Override
    public boolean isUserInRole(final String role) {
        return assignedRole.equals(role);
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String getAuthenticationScheme() {
        return null;
    }

}
