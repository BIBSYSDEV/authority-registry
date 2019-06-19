package no.bibsys.db;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    public DynamoDBEventProcessor(CloudsearchClient cloudsearchClient ) {
        super();
        this.cloudsearchClient = cloudsearchClient;
    }
    
    

    @Override
    public Void handleRequest(DynamodbEvent dynamodbEvent, Context context) {

        logger.debug("dynamodbEvent, #records={}", dynamodbEvent.getRecords().size());

        List<AmazonSdfDTO> documents = new ArrayList<>();

        for (DynamodbStreamRecord record : dynamodbEvent.getRecords()) {

            if (record == null) {
                continue;
            }

//            logger.debug("record.eventID={}, record.eventName={}, record.dynamodb={}", 
//                    record.getEventID(), record.getEventName(), record.getDynamodb());

            AmazonSdfDTO sdf = new AmazonSdfDTO(record.getEventName());
            sdf.setBody(record.getDynamodb().getNewImage().toString());
            
            documents.add(sdf);
        }

        cloudsearchClient.upsert(documents);
        return null;
    }

}

