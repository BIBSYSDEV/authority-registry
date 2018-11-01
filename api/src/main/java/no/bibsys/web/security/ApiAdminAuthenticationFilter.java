package no.bibsys.web.security;

import javax.ws.rs.ext.Provider;
import no.bibsys.EnvironmentReader;

@Provider
@RequireApiAdmin
public class ApiAdminAuthenticationFilter extends AbstractAuthenticationFilter {

    public ApiAdminAuthenticationFilter(EnvironmentReader environmentReader) {
        super(environmentReader, EnvironmentReader.API_ADMIN_API_KEY);
    }

}