package no.bibsys.db;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;

public class DynamoDBEventProcessor implements RequestHandler<DynamodbEvent, Void> {

    private static final String CLOUDSEARCH_DOCUMENT_ENDPOINT = "CLOUDSEARCH_DOCUMENT_ENDPOINT";
    private static final Logger logger = LoggerFactory.getLogger(DynamoDBEventProcessor.class);
    private transient CloudsearchClient cloudsearchClient;
    
    
    
    public DynamoDBEventProcessor() {
        super();
        String cloudsearchDomainName = System.getenv(CLOUDSEARCH_DOCUMENT_ENDPOINT);
        try {
            URL cloudsearchDocumentEndpointUrl = new URL(cloudsearchDomainName);
            this.cloudsearchClient = new CloudsearchClient(cloudsearchDocumentEndpointUrl);
        } catch (MalformedURLException e) {
            logger.error("",e);
        }
    }

    
    @Override
    public Void handleRequest(DynamodbEvent dynamodbEvent, Context context) {

        System.out.println("dynamodbEvent=" + dynamodbEvent);
        logger.debug("dynamodbEvent={}", dynamodbEvent);

        
        for (DynamodbStreamRecord record : dynamodbEvent.getRecords()) {

            if (record == null) {
                continue;
            }

            logger.debug("record.eventID={}, record.eventName={}, record.dynamodb={}", 
                    record.getEventID(), record.getEventName(), record.getDynamodb());
            if (record.getEventName().equals("MODIFY") || record.getEventName().equals("INSERT")) {
                cloudsearchClient.upsert(record.getEventSource());
            } else {
                logger.debug("handleRequest doesn't know what to do with eventName={}", record.getEventName());
            }
        }

        return null;
    }
    
}

