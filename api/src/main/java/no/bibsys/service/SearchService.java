package no.bibsys.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.cloudsearchdomain.AmazonCloudSearchDomain;
import com.amazonaws.services.cloudsearchdomain.AmazonCloudSearchDomainClientBuilder;
import com.amazonaws.services.cloudsearchdomain.model.QueryParser;
import com.amazonaws.services.cloudsearchdomain.model.SearchException;
import com.amazonaws.services.cloudsearchdomain.model.SearchRequest;
import com.amazonaws.services.cloudsearchdomain.model.SearchResult;

import no.bibsys.EnvironmentVariables;
import no.bibsys.aws.tools.Environment;

public class SearchService {

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

    public String simpleQuery(String registryName, String queryString) {
        logger.debug("Searching, endpoint={}, registryName={}, queryString={}", 
                this.serviceEndpoint, registryName, queryString);

        SearchRequest searchRequest = new SearchRequest()
                .withQuery(queryString)
                .withQueryParser(QueryParser.Simple);
        try {
            SearchResult searchResult = searchClient.search(searchRequest);
            logger.debug("searchResult={}", searchResult);
            return searchResult.toString();
        } catch (SearchException e) {
            logger.error("",e);
        }
        return "{}";
    }
}
