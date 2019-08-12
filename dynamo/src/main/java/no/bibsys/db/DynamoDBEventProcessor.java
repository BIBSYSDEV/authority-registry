package no.bibsys.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.OperationType;
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

    private final transient CloudsearchClient cloudsearchClient;
    private static final Logger logger = LoggerFactory.getLogger(DynamoDBEventProcessor.class);

    public DynamoDBEventProcessor() {
        cloudsearchClient = new CloudsearchClient();        
    }

    public DynamoDBEventProcessor(CloudsearchClient cloudsearchClient) {
        // For mocking
        this.cloudsearchClient = cloudsearchClient;        
    }


    @Override
    public Void handleRequest(DynamodbEvent dynamodbEvent, Context context) {

        try {
            logger.debug("dynamodbEvent, #records={}, dynamodbEvent={}", dynamodbEvent.getRecords().size(),
                    dynamodbEvent.toString());

            List<AmazonSdfDTO> documents = dynamodbEvent.getRecords().stream()
                    .map(this::createAmazonSdfFromTriggerEvent).collect(Collectors.toCollection(ArrayList::new));

            if (!documents.isEmpty()) {
                cloudsearchClient.uploadbatch(documents);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }


    private AmazonSdfDTO createAmazonSdfFromTriggerEvent(DynamodbStreamRecord dynamodDBStreamRecord) {
        try {


            StreamRecord streamRecord = dynamodDBStreamRecord.getDynamodb();

            AmazonSdfDTO sdf = new AmazonSdfDTO(dynamodDBStreamRecord.getEventName());
            if (streamRecord == null) {
                logger.debug("streamRecord == null");
            }

            if (OperationType.valueOf(dynamodDBStreamRecord.getEventName()) == OperationType.REMOVE) {
                logger.debug("OperationType.REMOVE, streamRecord={}",streamRecord);
                if (streamRecord.getKeys().containsKey("id")) {
                    sdf.setId(streamRecord.getKeys().get("id").getS());
                } else {
                    logger.error("Cannot get 'ID' from streamRecord for REMOVE operation");
                    return null;
                }
            } else {

                if (streamRecord.getNewImage() == null) {
                    logger.debug("streamRecord.getNewImage() == null");
                }
                if (streamRecord.getNewImage().containsKey("id")) {
                    sdf.setId(streamRecord.getNewImage().get("id").getS());
                }
                logger.debug("streamRecord={}",streamRecord);
                try {
                    Entity entity = extractFullEntity(streamRecord.getNewImage());
                    sdf.setFieldsFromEntity(entity);
                    logger.debug("sdf={}",sdf);
                } catch (Exception e) {
                    logger.error("",e);
                }
            }
            return sdf;
        } catch (Exception e) {
            logger.error("",e);
            throw new RuntimeException(e);
        }
    }

    private Entity extractFullEntity(Map<String, AttributeValue> map) {
        logger.debug("map={}",map);
        Entity entity = new Entity();
        ObjectNode body;
        try {
            ObjectMapper objectMapper = JsonUtils.newJsonParser();
            objectMapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            AttributeValue attributeValue = map.get("body");
            body = (ObjectNode) objectMapper.readTree(attributeValue.getS());

            entity.setBody(body);

            entity.setId(map.get("id").getS());
            entity.setCreated(map.get("created").getS());
            entity.setModified(map.get("modified").getS());

        } catch (IOException e) {
            logger.error("",e);
        }
        return entity;
    }

}

