package no.bibsys.db;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.cloudsearchdomain.AmazonCloudSearchDomain;
import com.amazonaws.services.cloudsearchdomain.AmazonCloudSearchDomainClientBuilder;
import com.amazonaws.services.cloudsearchdomain.model.ContentType;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsRequest;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsResult;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

import no.bibsys.utils.JsonUtils;

public class CloudsearchClient {


    private static final String CLOUDSEARCH_DOCUMENT_ENDPOINT_NAME = "CLOUDSEARCH_DOCUMENT_ENDPOINT";
    private static final String AWS_REGION_PROPERTY_NAME = "AWS_REGION";
    
    private static final Logger logger = LoggerFactory.getLogger(CloudsearchClient.class);
    private final transient AmazonCloudSearchDomain documentUploadClient;
    
    public CloudsearchClient() {

        String serviceEndpoint = System.getenv(CLOUDSEARCH_DOCUMENT_ENDPOINT_NAME).trim();
        String signingRegion = System.getenv(AWS_REGION_PROPERTY_NAME);
        
        logger.debug("documentUploadClient.serviceEndpoint='{}', signingRegion='{}'", serviceEndpoint, signingRegion);
        
        EndpointConfiguration endpointConfiguration = new EndpointConfiguration(serviceEndpoint, signingRegion);
        documentUploadClient = AmazonCloudSearchDomainClientBuilder.standard()
                .withEndpointConfiguration(endpointConfiguration).build();
    }
    
    public CloudsearchClient(AmazonCloudSearchDomain documentUploadClient) {
        // For mocking
        this.documentUploadClient = documentUploadClient;
    }

    public void uploadbatch(List<AmazonSdfDTO> documents) throws JsonGenerationException, JsonMappingException, IOException {
        
        UploadDocumentsRequest uploadDocumentsRequest = new UploadDocumentsRequest()
                .withContentType(ContentType.Applicationjson);
        
        String documentsAsString = batchToString(documents);
        logger.debug("documentsAsString={}",documentsAsString);

        byte[] bytes = documentsAsString.getBytes(Charsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        
        uploadDocumentsRequest.setDocuments(inputStream);
        uploadDocumentsRequest.setContentLength((long) bytes.length);
        
        UploadDocumentsResult uploadDocumentsResult = documentUploadClient.uploadDocuments(uploadDocumentsRequest);
        logger.debug("uploadDocumentsResult={}",uploadDocumentsResult);

    }

    private String batchToString(List<AmazonSdfDTO> documents) throws JsonGenerationException, JsonMappingException, IOException {
            StringWriter batchDocuments = new StringWriter();
            ObjectMapper objectMapper = JsonUtils.newJsonParser();
            objectMapper.writeValue(batchDocuments, documents);
            logger.debug("batchDocuments={}", batchDocuments);
            return batchDocuments.toString();
    }
}
