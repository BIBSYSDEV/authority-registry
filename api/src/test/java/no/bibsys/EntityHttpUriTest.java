package no.bibsys;

import no.bibsys.web.exception.MalformedEntityHttpUriException;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class EntityHttpUriTest {

    private static final String HTTPS_EXAMPLE_ORG = "https://example.org";
    private static final String REGISTRY = "registry";
    private static final String REGISTRY_ID = "registryId";
    private static final String ENTITY_ID = "entityId";
    private static final String ENTITY = "entity";
    private static final String EXPECTED_REGISTRY_HTTPS = "https://example.org/registry/registryId";
    private static final String EXPECTED_ENTITY_HTTPS = "https://example.org/registry/registryId/entity/entityId";
    private static final String WRONG = "wrong";

    @Test
    public void testRegistryPath_spread_valid() throws MalformedEntityHttpUriException {
        EntityHttpUri entityHttpUri = new EntityHttpUri(HTTPS_EXAMPLE_ORG, REGISTRY, REGISTRY_ID);
        assertEquals(EXPECTED_REGISTRY_HTTPS, entityHttpUri.asString());
    }

    @Test
    public void testEntityPath_spread_valid() throws MalformedEntityHttpUriException {
        EntityHttpUri entityHttpUri = new EntityHttpUri(HTTPS_EXAMPLE_ORG,
                REGISTRY, REGISTRY_ID, ENTITY, ENTITY_ID);
        assertEquals(EXPECTED_ENTITY_HTTPS, entityHttpUri.asString());
    }

    @Test(expected = MalformedEntityHttpUriException.class)
    public void testRegistryPath_spread_invalid() throws MalformedEntityHttpUriException {
        new EntityHttpUri(HTTPS_EXAMPLE_ORG, REGISTRY, REGISTRY_ID, WRONG);
    }

    @Test(expected = MalformedEntityHttpUriException.class)
    public void testRegistryPath_spreadValidPositions_invalid() throws MalformedEntityHttpUriException {
        new EntityHttpUri(HTTPS_EXAMPLE_ORG, REGISTRY, REGISTRY_ID, ENTITY);
    }

    @Test(expected = MalformedEntityHttpUriException.class)
    public void testEntityPath_spread_invalid() throws MalformedEntityHttpUriException {
        new EntityHttpUri(HTTPS_EXAMPLE_ORG, REGISTRY, REGISTRY_ID, ENTITY, ENTITY_ID, WRONG);
    }

    @Test
    public void testEntityPath_string_valid() throws MalformedEntityHttpUriException,
            MalformedURLException, URISyntaxException {
        EntityHttpUri entityHttpUri = new EntityHttpUri(EXPECTED_ENTITY_HTTPS);
        assertEquals(EXPECTED_ENTITY_HTTPS, entityHttpUri.asString());
    }
}
