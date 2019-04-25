package no.bibsys.web.security;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import org.junit.Test;

public class AssignedSecurityContextTest {

    private static final String JERSEY = "Jersey";

    @Test
    public void test() {
        AssignedSecurityContext securityContext = new AssignedSecurityContext("testRole");

        assertThat(securityContext.getUserPrincipal().getName(), is(JERSEY));
        assertThat(securityContext.isSecure(), is(false));
        assertThat(securityContext.getAuthenticationScheme(), is(nullValue()));
    }
}
