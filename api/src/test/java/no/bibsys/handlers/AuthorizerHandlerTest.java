package no.bibsys.handlers;

import java.util.HashMap;
import java.util.Map;
import no.bibsys.testtemplates.LocalTestApi;
import org.junit.Ignore;

public class AuthorizerHandlerTest extends LocalTestApi {


    private transient AuthorizerHandler authorizerHandler;


    @Ignore
    public void handleRequestTest() {
        authorizerHandler = new AuthorizerHandler(authenticationService);

        Map<String, Object> map = new HashMap<String, Object>();

        authorizerHandler.handleRequest(map, null);

    }
}