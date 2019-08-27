package no.bibsys;

import no.bibsys.web.exception.MalformedEntityHttpUriException;

import javax.ws.rs.core.UriBuilder;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class EntityHttpUri {

    private static final String PATH_SEPARATOR = "/";
    private static final String REGISTRY_STRING = "registry";
    private static final String ENTITY_STRING = "entity";
    private static final int REGISTRY_HTTP_URI_PATH_LENGTH = 2;
    private static final int ENTITY_HTTP_URI_PATH_LENGTH = 4;
    private static final String EMPTY_STRING = "";
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final int DEFAULT_HTTP_PORT = 80;
    private static final int DEFAULT_HTTPS_PORT = 443;
    private static final int REMOVE_FIRST_SEPARATOR = 1;
    private static final int REMOVE_DEFAULT_PORT = -1;
    private static final int EMPTY = 0;
    private static final int FIRST_ELEMENT = 0;
    private static final int THIRD_ELEMENT = 2;
    private static final String THE_PATH_ELEMENTS_WERE_EMPTY = "The path elements were empty";
    private static final String MALFORMED_REGISTRY_PATH = "The path did not conform to template registry/{registryId}";
    private static final String MALFORMED_ENTITY_PATH
            = "The path did not conform to template registry/{registryId}/entity/{entityId}";
    private final String namespace;
    private final String[] pathElements;
    private UriBuilder uriBuilder;

    EntityHttpUri(String namespace, String... pathElements) throws MalformedEntityHttpUriException {
        handlePathErrors(pathElements);
        this.namespace = namespace;
        this.pathElements = pathElements;
        setUriBuilder();
    }

    EntityHttpUri(String uri) throws URISyntaxException, MalformedURLException, MalformedEntityHttpUriException {
        URI localUri = new URI(uri);
        String path = localUri.getPath();
        String[] pathElements = path.substring(REMOVE_FIRST_SEPARATOR).split(PATH_SEPARATOR);

        handlePathErrors(pathElements);
        this.namespace = new URL(localUri.getScheme(),
                localUri.getHost(),
                removeDefaultPorts(localUri.getScheme(), localUri.getPort()),
                EMPTY_STRING).toString();
        this.pathElements = pathElements;
        setUriBuilder();
    }

    private int removeDefaultPorts(String scheme, int port) {
        if (scheme.equals(HTTP) && port == DEFAULT_HTTP_PORT || scheme.equals(HTTPS) && port == DEFAULT_HTTPS_PORT) {
            return REMOVE_DEFAULT_PORT;
        }
        return port;
    }

    private enum PathElements {
        REGISTRY(REGISTRY_STRING, FIRST_ELEMENT),
        ENTITY(ENTITY_STRING, THIRD_ELEMENT);

        PathElements(String elementString, int expectedPosition) {
            this.elementString = elementString;
            this.expectedPosition = expectedPosition;
        }

        String elementString;
        int expectedPosition;
    }

    private void handlePathErrors(String[] pathElements) throws MalformedEntityHttpUriException {
        String errors = getPathErrors(pathElements);
        if (nonNull(errors)) {
            throw new MalformedEntityHttpUriException(errors);
        }
    }
    private String getPathErrors(String[] pathElements) {

        if (isNull(pathElements) || pathElements.length == EMPTY) {
            return THE_PATH_ELEMENTS_WERE_EMPTY;
        }
        int length = pathElements.length;

        if (length < REGISTRY_HTTP_URI_PATH_LENGTH || !pathElements[PathElements.REGISTRY.expectedPosition]
                .equals(PathElements.REGISTRY.elementString)) {
            return MALFORMED_REGISTRY_PATH;
        }

        if (length > ENTITY_HTTP_URI_PATH_LENGTH || length == 3 ||
                (length == ENTITY_HTTP_URI_PATH_LENGTH
                        && !pathElements[PathElements.ENTITY.expectedPosition]
                        .equals(PathElements.ENTITY.elementString))) {
            return MALFORMED_ENTITY_PATH;
        }

        return null;
    }

    private void setUriBuilder() {
        this.uriBuilder = UriBuilder.fromUri(namespace);
        Arrays.stream(pathElements).forEach(uriBuilder::path);
    }

    public String asString() {
        return this.uriBuilder.toString();
    }

    public URI asURI() {
        return this.uriBuilder.build();
    }
}
