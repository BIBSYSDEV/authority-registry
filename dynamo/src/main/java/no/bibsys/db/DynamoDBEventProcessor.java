package no.bibsys.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.StreamRecord;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import no.bibsys.db.structures.Entity;
import no.bibsys.utils.JsonUtils;

public class DynamoDBEventProcessor implements RequestHandler<DynamodbEvent, Void> {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDBEventProcessor.class);
//    private final transient CloudsearchClient cloudsearchClient;
//    private static final String CLOUDSEARCH_SERVICE_ENDPOINT_NAME = "CLOUDSEARCH_SERVICE_ENDPOINT"; 
//    private static final String REGION = "eu-west-1";
    
//    public DynamoDBEventProcessor() {
//        super();
//        
//    }

//    public DynamoDBEventProcessor(CloudsearchClient cloudsearchClient ) {
//        super();
//        this.cloudsearchClient = cloudsearchClient;
//    }



    @Override
    public Void handleRequest(DynamodbEvent dynamodbEvent, Context context) {

        logger.debug("dynamodbEvent, #records={}", dynamodbEvent.getRecords().size());

        List<AmazonSdfDTO> documents = new ArrayList<>();

        for (DynamodbStreamRecord dynamodDBStreamRecord : dynamodbEvent.getRecords()) {

            if (dynamodDBStreamRecord == null) {
                continue;
            }
                
            String dynamDBEventName = dynamodDBStreamRecord.getEventName();
            StreamRecord streamRecord = dynamodDBStreamRecord.getDynamodb();
            AmazonSdfDTO amazonSdfFromTriggerEvent;
            try {
                amazonSdfFromTriggerEvent = createAmazonSdfFromTriggerEvent(dynamDBEventName, streamRecord);
                documents.add(amazonSdfFromTriggerEvent);
            } catch (IOException e) {
                logger.error("",e);
            }
        }

        new CloudsearchClient().upsert(documents);
        return null;
    }

    private AmazonSdfDTO createAmazonSdfFromTriggerEvent(String dynamoDBEventName, StreamRecord streamRecord) throws IOException {
        AmazonSdfDTO sdf = new AmazonSdfDTO(dynamoDBEventName);
        logger.debug("dynamoDBEventName={}, streamRecord= {}",dynamoDBEventName, streamRecord);
        if (streamRecord.getNewImage().containsKey("id")) {
            sdf.setId(streamRecord.getNewImage().get("id").getS());
        }
        sdf.setFieldsFromEntity(extractEntity(streamRecord.getNewImage()));
        return sdf;
    }

    private Entity extractEntity(Map<String, AttributeValue> map) {
        Entity entity = new Entity();
        ObjectNode body;
        try {
            ObjectMapper objectMapper = JsonUtils.newJsonParser();
            objectMapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            AttributeValue attributeValue = map.get("body");
            body = (ObjectNode) objectMapper.readTree(attributeValue.getS());
            entity.setBody(body);
        } catch (IOException e) {
            logger.error("",e);
        }
        return entity;
    }

}

