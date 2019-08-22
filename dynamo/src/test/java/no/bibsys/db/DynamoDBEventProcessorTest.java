package no.bibsys.db;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.amazonaws.services.cloudsearchdomain.AmazonCloudSearchDomain;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsRequest;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;

import no.bibsys.db.structures.Entity;

public class DynamoDBEventProcessorTest {

    private static final String TESTENTITY_ID = "https://qpshvtds48.execute-api.eu-west-1.amazonaws.com/final/registry/tekord-r/entity/00b67e45-e6a0-41d3-adc1-0e95652419e9";

    DynamoDBEventProcessor dynamoDBEventProcessor;

    @Before
    public void init() throws IOException {
       
        AmazonCloudSearchDomain cloudSearchDomainMock = Mockito.mock(AmazonCloudSearchDomain.class);
        CloudsearchDocumentClient cloudsearchClientMock = new CloudsearchDocumentClient(cloudSearchDomainMock);
        UploadDocumentsResult uploadDockumentsResponeMock = new UploadDocumentsResult();
        uploadDockumentsResponeMock.setStatus("Okidoi, mocked");
        when(cloudSearchDomainMock.uploadDocuments(any(UploadDocumentsRequest.class)))
                .thenReturn((uploadDockumentsResponeMock));

        dynamoDBEventProcessor = new DynamoDBEventProcessor(cloudsearchClientMock);
    }

    @Ignore
    @Test
    public void testHandleRequest() throws IOException {
        DynamodbEvent dynamodbEvent = SampleEventData.sampleDynamoDBEvent();
        Context context = null;
        dynamoDBEventProcessor.handleRequest(dynamodbEvent, context);
    }
    
    @Ignore
    @Test
    public void testGetEntity() throws IOException {
        Entity entity = dynamoDBEventProcessor.getEntity(TESTENTITY_ID);
        assertNotNull(entity);
    }
    
}
