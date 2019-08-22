package no.bibsys.db;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.OperationType;
import com.amazonaws.services.dynamodbv2.model.StreamRecord;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;

import no.bibsys.utils.IoUtils;

public class SampleEventData {


    private static final String SAMPLE_RECORD_IDENTIFIER = "dsr1";
    
    private static final String SAMPLE_ENTITY_URI = "https://qpshvtds48.execute-api.eu-west-1.amazonaws.com/final/registry/tekord-r/entity/00b67e45-e6a0-41d3-adc1-0e95652419e9";
    
    private static String sampleJsonRecordBody() throws IOException { 
        String bodyString = IoUtils.resourceAsString(Paths.get("json", "sampleEventRecordBody.json"));
//        ObjectNode body = (ObjectNode) JsonUtils.newJsonParser().readTree(bodyString);
        
        return bodyString;
    }

    
    

    public static DynamodbEvent sampleDynamoDBEvent() throws IOException {
     
        Date now = new Date();
        String nowStr = now.toString();
        DynamodbEvent dynamodbEvent = new DynamodbEvent();
        
        List<DynamodbStreamRecord> eventStreamRecords = new ArrayList<>();
        
        DynamodbStreamRecord dynamodbStreamRecord = createDynamodbStreamRecord(nowStr);
        eventStreamRecords.add(dynamodbStreamRecord);

        DynamodbStreamRecord dynamodbStreamRecordRemove = createDynamodbStreamRecord(nowStr);
        dynamodbStreamRecordRemove.setEventName(OperationType.REMOVE);
        eventStreamRecords.add(dynamodbStreamRecordRemove);

        
        dynamodbEvent.setRecords(eventStreamRecords);
        
        return dynamodbEvent;
    }




    private static DynamodbStreamRecord createDynamodbStreamRecord(String nowStr) throws IOException {
        DynamodbStreamRecord dynamodbStreamRecord = new DynamodbStreamRecord();
        
        StreamRecord streamRecord = createStreamRecord(nowStr);
        
        dynamodbStreamRecord.setDynamodb(streamRecord);
        dynamodbStreamRecord.setEventVersion("1.1");
        dynamodbStreamRecord.setEventSource("aws:dynamodb");
        
        dynamodbStreamRecord.setEventName("INSERT");
        return dynamodbStreamRecord;
    }




    private static StreamRecord createStreamRecord(String nowStr) throws IOException {
        StreamRecord dynamodbStreamRecord = new StreamRecord();
        dynamodbStreamRecord.addKeysEntry("id", new AttributeValue(SAMPLE_RECORD_IDENTIFIER));
        Map<String, AttributeValue> newImageMap = new HashMap<>();
        newImageMap.put("created", new AttributeValue(nowStr));
        newImageMap.put("modified", new AttributeValue(nowStr));
        newImageMap.put("id", new AttributeValue(SAMPLE_ENTITY_URI));
        newImageMap.put("body", new AttributeValue(sampleJsonRecordBody()));
        dynamodbStreamRecord.setNewImage(newImageMap);
        return dynamodbStreamRecord;
    }
            
    
    
}
