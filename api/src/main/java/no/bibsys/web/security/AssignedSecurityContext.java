package no.bibsys.web.security;

import java.security.Principal;
import javax.ws.rs.core.SecurityContext;

public class AssignedSecurityContext implements SecurityContext {

    private final transient String asssignedRole;
    
    public AssignedSecurityContext(String asssignedRole) {
        this.asssignedRole = asssignedRole;
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
        return asssignedRole.equals(role);
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
