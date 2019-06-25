package no.bibsys.db;

import java.io.ByteArrayInputStream;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Charsets;

import no.bibsys.utils.JsonUtils;

public class CloudsearchClient {

    private static final Logger logger = LoggerFactory.getLogger(CloudsearchClient.class);
    private final transient AmazonCloudSearchDomain cloudseachDomainClient;

    public CloudsearchClient(String region, String serviceEndpoint) {
        this(AmazonCloudSearchDomainClientBuilder.standard().withEndpointConfiguration(new EndpointConfiguration(serviceEndpoint,region)).build());
    }
    
    public CloudsearchClient(AmazonCloudSearchDomain cloudSearchDomain) {
        cloudseachDomainClient = cloudSearchDomain;
        logger.debug("CloudsearchClient, with cloudSearchDomain=",cloudSearchDomain);
    }

    
    public void upsert(List<AmazonSdfDTO> documents) {
        
        UploadDocumentsRequest uploadDocumentsRequest = new UploadDocumentsRequest()
                .withContentType(ContentType.Applicationjson)
                .withDocuments(batchToInputStream(documents));
        
        UploadDocumentsResult uploadDocumentsResult = cloudseachDomainClient.uploadDocuments(uploadDocumentsRequest);
        logger.debug("uploadDocumentsResult={}",uploadDocumentsResult);

    }

    private InputStream batchToInputStream(List<AmazonSdfDTO> documents) {
        try {
            StringWriter batchDocuments = new StringWriter();
            ObjectMapper objectMapper = JsonUtils.newJsonParser();
            ArrayNode documentsArrayNode = objectMapper.createArrayNode();
            for (AmazonSdfDTO amazonAddSdfDTO : documents) {
                documentsArrayNode.add(objectMapper.writeValueAsString(amazonAddSdfDTO));
            }
            batchDocuments.write(objectMapper.writeValueAsString(documentsArrayNode));
            logger.debug("batchDocuments={}", batchDocuments);
            return new ByteArrayInputStream(batchDocuments.toString().getBytes(Charsets.UTF_8)); 
        } catch (JsonProcessingException e) {
            logger.error("",e);
            return null;
        } 
    }

}
