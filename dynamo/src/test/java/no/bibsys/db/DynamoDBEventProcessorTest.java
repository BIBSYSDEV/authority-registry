package no.bibsys.db;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.amazonaws.services.cloudsearchdomain.AmazonCloudSearchDomain;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsRequest;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;

public class DynamoDBEventProcessorTest {

    DynamoDBEventProcessor dynamoDBEventProcessor;
    
    @Before
    public void init() throws IOException {
        
        AmazonCloudSearchDomain cloudSearchDomainMock = Mockito.mock(AmazonCloudSearchDomain.class);
        CloudsearchClient cloudsearchClient = new CloudsearchClient(cloudSearchDomainMock);  
        UploadDocumentsResult uploadDockumentsResponeMock = new UploadDocumentsResult();
        uploadDockumentsResponeMock.setStatus("Okidoi, mocked");
        when(cloudSearchDomainMock.uploadDocuments(any(UploadDocumentsRequest.class))).thenReturn((uploadDockumentsResponeMock));
        
        dynamoDBEventProcessor = new DynamoDBEventProcessor(cloudsearchClient);
    }
    
    @Test
    public void testHandleRequest() throws IOException {
        DynamodbEvent dynamodbEvent = SampleEventData.sampleDynamoDBEvent();
        Context context = null;
        dynamoDBEventProcessor.handleRequest(dynamodbEvent, context);
    }

}
