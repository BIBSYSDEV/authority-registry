package no.bibsys.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.cloudsearchdomain.AmazonCloudSearchDomain;
import com.amazonaws.services.cloudsearchdomain.AmazonCloudSearchDomainClientBuilder;
import com.amazonaws.services.cloudsearchdomain.model.Hit;
import com.amazonaws.services.cloudsearchdomain.model.Hits;
import com.amazonaws.services.cloudsearchdomain.model.QueryParser;
import com.amazonaws.services.cloudsearchdomain.model.SearchException;
import com.amazonaws.services.cloudsearchdomain.model.SearchRequest;
import com.amazonaws.services.cloudsearchdomain.model.SearchResult;

import no.bibsys.EnvironmentVariables;
import no.bibsys.aws.tools.Environment;

public class SearchService {

    private static final String FILTERQUERY_BASE = "inscheme:'%s'";
    private static final String CLOUDSEARCH_RETURN_FIELD = "presentation_json";
    private transient final String serviceEndpoint;
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private static final String AWS_REGION_PROPERTY_NAME = "AWS_REGION";
    private final transient AmazonCloudSearchDomain searchClient;


    public SearchService(Environment environmentReader) {
        this.serviceEndpoint = environmentReader.readEnv(EnvironmentVariables.CLOUDSEARCH_SEARCH_ENDPOINT);

        String signingRegion = System.getenv(AWS_REGION_PROPERTY_NAME);

        logger.debug("searchEndpoint={}", this.serviceEndpoint);
        logger.debug("SearchService.serviceEndpoint='{}', signingRegion='{}'", serviceEndpoint, signingRegion);

        EndpointConfiguration endpointConfiguration = new EndpointConfiguration(serviceEndpoint, signingRegion);
        searchClient = AmazonCloudSearchDomainClientBuilder.standard()
                .withEndpointConfiguration(endpointConfiguration).build();


    }

    public List<String> simpleQuery(String registryName, String queryString) {
        logger.debug("Searching, endpoint={}, registryName={}, queryString={}", 
                this.serviceEndpoint, registryName, queryString);
        
        String filterQuery = String.format(FILTERQUERY_BASE,registryName);
        SearchRequest searchRequest = new SearchRequest()
                .withQuery(queryString)
                .withFilterQuery(filterQuery)
                .withReturn(CLOUDSEARCH_RETURN_FIELD)
                .withQueryParser(QueryParser.Simple);
        try {
            logger.debug("searchRequest={}", searchRequest);
            List<String> result = new ArrayList<>();
            SearchResult searchResult = searchClient.search(searchRequest);
            logger.debug("searchResult={}", searchResult);
             Hits hits = searchResult.getHits();
             for (Hit hit : hits.getHit()) {
                result.addAll(hit.getFields().get(CLOUDSEARCH_RETURN_FIELD));
            }
             logger.debug("result={}", result);
            return result;
        } catch (SearchException e) {
            logger.error("",e);
        }
        return null;
    }
}
