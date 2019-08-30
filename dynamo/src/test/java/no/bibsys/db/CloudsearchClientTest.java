package no.bibsys.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.amazonaws.services.cloudsearchdomain.AmazonCloudSearchDomain;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsRequest;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsResult;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import no.bibsys.db.structures.Entity;
import no.bibsys.utils.JsonUtils;

public class CloudsearchClientTest {

    private static final String sampleIdentifier = "identifier01";
    private static final String sampleDynamoDBIdentifier = "dynamooDB-identifier01";
    
    CloudsearchDocumentClient cloudsearchClient;
    AmazonCloudSearchDomain amazonCloudSearchDomainMock;
    
    private AmazonSdfDTO createSampleAmazonSDF() throws IOException {
        AmazonSdfDTO amazonSdfDTO = new AmazonSdfDTO(AmazonSdfDTO.EventName.INSERT.name());
        amazonSdfDTO.setId(sampleDynamoDBIdentifier);
        Entity entity = new SampleData().bigsampleEntity();
        
        String presentationString = entity.toString();
        amazonSdfDTO.setField(AmazonSdfDTO.CLOUDSEARCH_PRESENTATION_FIELD, presentationString);
        return amazonSdfDTO;
    }
    
    
    @Before
    public void init() throws IOException {
        amazonCloudSearchDomainMock = Mockito.mock(AmazonCloudSearchDomain.class);
        UploadDocumentsResult uploadDockumentsResponeMock = new UploadDocumentsResult();
        uploadDockumentsResponeMock.setStatus("Okidoi, mocked");
        when(amazonCloudSearchDomainMock.uploadDocuments(any(UploadDocumentsRequest.class)))
            .thenReturn((uploadDockumentsResponeMock));
        cloudsearchClient = new CloudsearchDocumentClient(amazonCloudSearchDomainMock);
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
        assertNotNull(amazonSdfDTO.getFields().get(AmazonSdfDTO.CLOUDSEARCH_PRESENTATION_FIELD));
    }
    
    @Test
    public void testCreateAmazonSDO4() throws IOException {
        String entitySource = new SampleData().bigsampleEntity4();

        ObjectMapper objectMapper = JsonUtils.newJsonParser();
        objectMapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        ObjectNode objectNode = (ObjectNode)objectMapper.readTree(entitySource);
        Iterator<Entry<String, JsonNode>> fields = objectNode.fields();

        AmazonSdfDTO amazonSdfDTO = new AmazonSdfDTO(AmazonSdfDTO.EventName.INSERT.name());
        fields.forEachRemaining(e ->  amazonSdfDTO.setField(e.getKey(), e.getValue()));
        amazonSdfDTO.setField(AmazonSdfDTO.CLOUDSEARCH_PRESENTATION_FIELD, entitySource);
        
        assertNotNull(amazonSdfDTO.getFields().get(AmazonSdfDTO.CLOUDSEARCH_PRESENTATION_FIELD));
        
    }
   
}
