package no.bibsys.service;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.bibsys.web.model.QueryResultDto;

public class SearchService {

    private transient final URL searchEndpoint;
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    public SearchService(URL searchEndpoint) {
        this.searchEndpoint = searchEndpoint;
        logger.debug("searchEndpoint={}", this.searchEndpoint);
    }

    public QueryResultDto simpleQuery(String registryName, String queryString, String scheme) {
        logger.debug("Searching, endpoint={}, registryName={}, queryString={}, scheme={}", this.searchEndpoint,
                registryName, queryString, scheme);
        return null;
    }
}
