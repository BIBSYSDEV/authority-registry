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

import no.bibsys.aws.tools.Environment;
import no.bibsys.utils.JsonUtils;



public class CloudsearchDocumentClient {

    public static final String CLOUDSEARCH_DOCUMENT_ENDPOINT = "CLOUDSEARCH_DOCUMENT_ENDPOINT";
    
    private static final String AWS_REGION_PROPERTY_NAME = "AWS_REGION";
    
    private static final Logger logger = LoggerFactory.getLogger(CloudsearchDocumentClient.class);
    private final transient AmazonCloudSearchDomain documentUploadClient;
    
    public CloudsearchDocumentClient() {

        String serviceEndpoint = new Environment().readEnv(CLOUDSEARCH_DOCUMENT_ENDPOINT).trim();
        String signingRegion = System.getenv(AWS_REGION_PROPERTY_NAME);
        
        logger.debug("documentUploadClient.serviceEndpoint='{}', signingRegion='{}'", serviceEndpoint, signingRegion);
        
        EndpointConfiguration endpointConfiguration = new EndpointConfiguration(serviceEndpoint, signingRegion);
        documentUploadClient = AmazonCloudSearchDomainClientBuilder.standard()
                .withEndpointConfiguration(endpointConfiguration).build();
    }
    
    public CloudsearchDocumentClient(AmazonCloudSearchDomain documentUploadClient) {
        // For mocking
        this.documentUploadClient = documentUploadClient;
    }

    public void uploadbatch(List<AmazonSdfDTO> documents)
            throws JsonGenerationException, JsonMappingException, IOException {
        
        logger.debug("uploadbatch -> documents={}", documents);
        
        if (documents.isEmpty()) {
            logger.debug("documents,isEmpty(), skipping batch");
            return;
        }
        
        UploadDocumentsRequest uploadDocumentsRequest = new UploadDocumentsRequest()
                .withContentType(ContentType.Applicationjson);
        
        String documentsAsString = batchToString(documents);
        if (documentsAsString == null || documentsAsString.isEmpty()) {
            logger.debug("documentsAsString={}, skipping",documentsAsString);
        }

        byte[] bytes = documentsAsString.getBytes(Charsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        
        uploadDocumentsRequest.setDocuments(inputStream);
        uploadDocumentsRequest.setContentLength((long) bytes.length);
        
        UploadDocumentsResult uploadDocumentsResult = documentUploadClient.uploadDocuments(uploadDocumentsRequest);
        logger.debug("uploadDocumentsResult={}",uploadDocumentsResult);

    }

    public  String simpleSearch(String query) {
        return "";
    }
    

    
    
    
    private String batchToString(List<AmazonSdfDTO> documents)
            throws JsonGenerationException, JsonMappingException, IOException {
        StringWriter batchDocuments = new StringWriter();
        if (!documents.isEmpty()) {
            ObjectMapper objectMapper = JsonUtils.newJsonParser();
            objectMapper.writeValue(batchDocuments, documents);
        }
        logger.debug("batchDocuments={}", batchDocuments);
        return batchDocuments.toString();
    }
}
