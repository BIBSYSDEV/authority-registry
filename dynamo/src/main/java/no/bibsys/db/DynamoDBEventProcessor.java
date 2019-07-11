package no.bibsys.db;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        logger.debug("dynamodbEvent, #records={}", dynamodbEvent.getRecords().size());

        List<AmazonSdfDTO> documents = dynamodbEvent.getRecords().stream()
                .map(this::createAmazonSdfFromTriggerEvent)
                .collect(Collectors.toCollection(ArrayList::new));

        try {
            cloudsearchClient.uploadbatch(documents);
        } catch (IOException e) {
            logger.error("",e);
        }
        return null;
    }


    private AmazonSdfDTO createAmazonSdfFromTriggerEvent(DynamodbStreamRecord dynamodDBStreamRecord) {
        StreamRecord streamRecord = dynamodDBStreamRecord.getDynamodb();

        AmazonSdfDTO sdf = new AmazonSdfDTO(dynamodDBStreamRecord.getEventName());
        if (streamRecord.getNewImage().containsKey("id")) {
            sdf.setId(streamRecord.getNewImage().get("id").getS());
        }
        try {
            sdf.setFieldsFromEntity(extractFullEntity(streamRecord.getNewImage()));
        } catch (IOException e) {
            logger.error("",e);
        }
        return sdf;
    }

    private Entity extractFullEntity(Map<String, AttributeValue> map) {
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

