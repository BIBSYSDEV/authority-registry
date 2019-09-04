package no.bibsys.web;

import org.junit.Test;

import javax.ws.rs.core.Response;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JsonLdContextResourceTest {

    @Test
    public void itExists() {
        new JsonLdContextResource();
    }

    @Test
    public void itReturnsJsonLdContextObject() throws IOException {
        JsonLdContextResource jsonLdContextResource = new JsonLdContextResource();
        Response jsonLdContextResponse = jsonLdContextResource.getJsonLdContext();
        assertNotNull(jsonLdContextResponse);
        assertEquals(200, jsonLdContextResponse.getStatus());
    }

}
