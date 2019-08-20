package no.bibsys.service;

import java.io.StringWriter;
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

    private static final String FILTERQUERY_BASE = "inscheme:'http://unit.no/system#%s'";
    private static final String CLOUDSEARCH_RETURN_FIELD = "presentation_json";
    private static final String AWS_REGION_PROPERTY_NAME = "AWS_REGION";

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private transient final String serviceEndpoint;
    private final transient AmazonCloudSearchDomain searchClient;

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

        String filterQuery = String.format(FILTERQUERY_BASE,registryName);

        SearchRequest searchRequest = new SearchRequest()
                .withQuery(queryString)
                .withFilterQuery(filterQuery)
                .withReturn(CLOUDSEARCH_RETURN_FIELD)
                .withQueryParser(QueryParser.Simple);
        try {
            logger.debug("searchRequest={}", searchRequest);
            SearchResult searchResult = searchClient.search(searchRequest);
            logger.debug("searchResult={}", searchResult);
            Hits hits = searchResult.getHits();
            StringWriter result = new StringWriter();
            result.append("[");
            for (Hit hit : hits.getHit()) {
                List<String> list = hit.getFields().get(CLOUDSEARCH_RETURN_FIELD);
                for (String presentationJson : list) {
                    result.append(presentationJson).append(",");
                }
            }
            result.append("]");
            // Removing the last ','
            return result.toString().replace(",]", "]");
        } catch (SearchException e) {
            logger.error("",e);
            throw e;
        }
    }
}
