package no.bibsys.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.amazonaws.services.cloudsearchdomain.AmazonCloudSearchDomain;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsRequest;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsResult;

import no.bibsys.db.structures.Entity;

public class CloudsearchClientTest {

    private static final String sampleIdentifier = "identifier01";
    private static final String sampleDynamoDBIdentifier = "dynamooDB-identifier01";
    
    CloudsearchClient cloudsearchClient;
    AmazonCloudSearchDomain amazonCloudSearchDomainMock;
    
    private AmazonSdfDTO createSampleAmazonSDF() throws IOException {
        AmazonSdfDTO amazonSdfDTO = new AmazonSdfDTO(AmazonSdfDTO.EventName.INSERT.name());
        amazonSdfDTO.setId(sampleDynamoDBIdentifier);
        Entity entity = new SampleData().bigsampleEntity();
        amazonSdfDTO.setFieldsFromEntity(entity);
        return amazonSdfDTO;
    }
    
    
    @Before
    public void init() throws IOException {
        amazonCloudSearchDomainMock = Mockito.mock(AmazonCloudSearchDomain.class);
        UploadDocumentsResult uploadDockumentsResponeMock = new UploadDocumentsResult();
        uploadDockumentsResponeMock.setStatus("Okidoi, mocked");
        when(amazonCloudSearchDomainMock.uploadDocuments(any(UploadDocumentsRequest.class)))
            .thenReturn((uploadDockumentsResponeMock));
        cloudsearchClient = new CloudsearchClient(amazonCloudSearchDomainMock);
    }
    
    
    @Test
    public void testCreateAmazonSDF() throws IOException {
        AmazonSdfDTO amazonSdfDTO = createSampleAmazonSDF();
        System.out.println(amazonSdfDTO.toString());
        assertEquals(sampleDynamoDBIdentifier, amazonSdfDTO.getId());
        assertNotEquals(sampleIdentifier, amazonSdfDTO.getId());
    }


    @Test
    public void testUpsert() throws IOException {
        AmazonSdfDTO amazonSdfDTO = createSampleAmazonSDF();
        List<AmazonSdfDTO> documents = new ArrayList<>();
        documents.add(amazonSdfDTO);
        cloudsearchClient.uploadbatch(documents);
        
    }

    @Test
    public void testCreateAmazonSDFAndCheckPresentation() throws IOException {
        AmazonSdfDTO amazonSdfDTO = createSampleAmazonSDF();
        assertNotNull(amazonSdfDTO.getFields().get(AmazonSdfDTO.CLOUDSEARCH_PRESENTAION_FIELD));
        System.out.println(amazonSdfDTO.toString());
    }
    
    
    
    
}
