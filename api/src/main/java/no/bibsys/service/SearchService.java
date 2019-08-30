package no.bibsys.service;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.cloudsearchdomain.AmazonCloudSearchDomain;
import com.amazonaws.services.cloudsearchdomain.AmazonCloudSearchDomainClientBuilder;
import com.amazonaws.services.cloudsearchdomain.model.Hits;
import com.amazonaws.services.cloudsearchdomain.model.QueryParser;
import com.amazonaws.services.cloudsearchdomain.model.SearchException;
import com.amazonaws.services.cloudsearchdomain.model.SearchRequest;
import com.amazonaws.services.cloudsearchdomain.model.SearchResult;

import no.bibsys.EnvironmentVariables;
import no.bibsys.aws.tools.Environment;

public class SearchService {

    private static final String EMPTY_STRING = "";
    private static final String COMMA_SEPARATOR = ",";
    private static final String JSON_START_ARRAY = "[";
    private static final String JSON_END_ARRAY = "]";
    private static final String CLOUDSEARCH_RETURN_FIELD = "presentation_json";
    private static final String AWS_REGION_PROPERTY_NAME = "AWS_REGION";

    private final transient String serviceEndpoint;
    private final transient AmazonCloudSearchDomain searchClient;

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    public SearchService(Environment environmentReader) {
        this.serviceEndpoint = environmentReader.readEnv(EnvironmentVariables.CLOUDSEARCH_SEARCH_ENDPOINT);

        String signingRegion = System.getenv(AWS_REGION_PROPERTY_NAME);

        logger.debug("SearchService.serviceEndpoint='{}', signingRegion='{}'", this.serviceEndpoint, signingRegion);

        EndpointConfiguration endpointConfiguration = new EndpointConfiguration(serviceEndpoint, signingRegion);
        searchClient = AmazonCloudSearchDomainClientBuilder.standard()
                .withEndpointConfiguration(endpointConfiguration).build();
    }

    public String simpleQuery(String registryName, String queryString) {
        logger.debug("Searching, endpoint={}, registryName={}, queryString={}", 
                this.serviceEndpoint, registryName, queryString);

        SearchRequest searchRequest = new SearchRequest()
                .withQuery(queryString)
                .withReturn(CLOUDSEARCH_RETURN_FIELD)
                .withQueryParser(QueryParser.Simple);
        
        try {
            SearchResult searchResult = searchClient.search(searchRequest);
            Hits hits = searchResult.getHits();
            return hitToString(hits);
        } catch (SearchException e) {
            logger.error(EMPTY_STRING,e);
            throw e;
        }
    }

    private String hitToString(Hits hits) {
        return JSON_START_ARRAY + String.join(COMMA_SEPARATOR,
                hits.getHit().stream()
                        .map(hit -> hit.getFields().get(CLOUDSEARCH_RETURN_FIELD).stream().findFirst().get())
                        .collect(Collectors.toList()))
                + JSON_END_ARRAY;
    }

}
