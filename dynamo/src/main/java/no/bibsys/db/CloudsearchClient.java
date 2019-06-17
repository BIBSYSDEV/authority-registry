package no.bibsys.db;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudsearchClient {

    private final transient URL endpoint;
    private static final Logger logger = LoggerFactory.getLogger(CloudsearchClient.class);
    
    public CloudsearchClient(URL cloudsearchDocumentEndpointUrl) {
        super();
        this.endpoint = cloudsearchDocumentEndpointUrl;
        logger.debug("CloudsearchClient setting Cs endpoint to ={}", cloudsearchDocumentEndpointUrl);
    }

    public void upsert(String eventSource) {
        logger.debug("updating CloudSearch@{} with record = {}" + eventSource, endpoint, eventSource);
    }

}
