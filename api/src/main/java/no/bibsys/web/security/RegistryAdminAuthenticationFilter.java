package no.bibsys.web.security;

import javax.ws.rs.ext.Provider;
import no.bibsys.EnvironmentReader;

@Provider
@RequireRegistryAdmin
public class RegistryAdminAuthenticationFilter extends AbstractAuthenticationFilter {

    public RegistryAdminAuthenticationFilter(EnvironmentReader environmentReader) {
        super(environmentReader, EnvironmentReader.REGISTRY_ADMIN_API_KEY);
    }

}