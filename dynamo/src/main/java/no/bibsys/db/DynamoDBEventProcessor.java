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

        for (DynamodbStreamRecord record : dynamodbEvent.getRecords()) {

            if (record == null) {
                continue;
            }

            logger.debug("record.eventID=%s, record.eventName=%s, record.dynamodb=%s", record.getEventID(), record.getEventName(), record.getDynamodb());
        }

        return null;
    }
    
    
    
    
    

}

