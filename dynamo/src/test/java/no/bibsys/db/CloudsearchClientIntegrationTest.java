package no.bibsys.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.amazonaws.services.cloudsearchdomain.AmazonCloudSearchDomain;

import no.bibsys.db.structures.Entity;

public class CloudsearchClientIntegrationTest {

    private static final String sampleDynamoDBIdentifier = "test-identifier-01";
    
    CloudsearchDocumentClient cloudsearchClient;
    AmazonCloudSearchDomain amazonCloudSearchDomainMock;
    
    private AmazonSdfDTO createSampleAmazonSDF() throws IOException {
        AmazonSdfDTO amazonSdfDTO = new AmazonSdfDTO(AmazonSdfDTO.EventName.INSERT.name());
        amazonSdfDTO.setId(sampleDynamoDBIdentifier);
        Entity entity = new SampleData().bigsampleEntity();
//        amazonSdfDTO.setFieldsFromEntity(entity);
        return amazonSdfDTO;
    }
    
    
    @Before
    public void init() throws IOException {
        cloudsearchClient = new CloudsearchDocumentClient();
    }
    
    

    @Ignore
    @Test
    public void testUpsert() throws IOException {
        AmazonSdfDTO amazonSdfDTO = createSampleAmazonSDF();
        List<AmazonSdfDTO> documents = new ArrayList<>();
        documents.add(amazonSdfDTO);
        cloudsearchClient.uploadbatch(documents);
    }
    

    @Ignore
    @Test
    public void testDelete() throws IOException {
        AmazonSdfDTO amazonSdfDTO = new AmazonSdfDTO(AmazonSdfDTO.CloudsearchOperation.DELETE);
        amazonSdfDTO.setId(sampleDynamoDBIdentifier);
        List<AmazonSdfDTO> documents = new ArrayList<>();
        documents.add(amazonSdfDTO);
        cloudsearchClient.uploadbatch(documents);
    }

    
}
