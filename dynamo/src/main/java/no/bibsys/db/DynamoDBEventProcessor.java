package no.bibsys.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;

public class DynamoDBEventProcessor implements RequestHandler<DynamodbEvent, Void> {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDBEventProcessor.class);
    
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
            if (record.getEventName().equals("MODIFY")) {
                System.out.println("updating ALMA for record =" + record.getEventSource());        
            }
        }

        return null;
    }
    
}

