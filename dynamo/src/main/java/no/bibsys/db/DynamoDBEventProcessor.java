package no.bibsys.db;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.model.StreamRecord;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;

import no.bibsys.aws.tools.Environment;
import no.bibsys.db.AmazonSdfDTO.CloudsearchOperation;
import no.bibsys.db.AmazonSdfDTO.EventName;
import no.bibsys.db.structures.Entity;

public class DynamoDBEventProcessor implements RequestHandler<DynamodbEvent, Void> {

//    private static final String DYNAMODB_MODIFIED_FIELD = "modified";
//    private static final String DYNAMODB_CREATED_FIELD = "created";
//    private static final String DYNAMODB_BODY_FIELD = "body";
    private static final String DYNAMODB_ID_FIELD = "id";
    private static final String RESTAPI_URL = "RESTAPI_URL";
    private final transient CloudsearchDocumentClient cloudsearchDocumentClient;
    private final transient String restApiUrl; 
    private static final Logger logger = LoggerFactory.getLogger(DynamoDBEventProcessor.class);

    public DynamoDBEventProcessor() {
        cloudsearchDocumentClient = new CloudsearchDocumentClient();
        restApiUrl = new Environment().readEnv(RESTAPI_URL);
    }

    public DynamoDBEventProcessor(CloudsearchDocumentClient cloudsearchClient) {
        // For mocking
        this.cloudsearchDocumentClient = cloudsearchClient;        
        restApiUrl = new Environment().readEnv(RESTAPI_URL);
    }

    @Override
    public Void handleRequest(DynamodbEvent dynamodbEvent, Context context) {

        try {
            logger.debug("dynamodbEvent, #records={}, dynamodbEvent={}", dynamodbEvent.getRecords().size(),
                    dynamodbEvent.toString());

            List<AmazonSdfDTO> documents = dynamodbEvent.getRecords().stream()
                    .map(this::createAmazonSdfFromTriggerEvent).collect(Collectors.toCollection(ArrayList::new));

            if (!documents.isEmpty()) {
                cloudsearchDocumentClient.uploadbatch(documents);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }


    private AmazonSdfDTO createAmazonSdfFromTriggerEvent(DynamodbStreamRecord dynamodDBStreamRecord) {
        try {
            StreamRecord streamRecord = dynamodDBStreamRecord.getDynamodb();
            if (streamRecord == null) {
                logger.warn("streamRecord == null, skipping this event. incomuing DynamodbStreamRecord={}",
                        dynamodDBStreamRecord);
                return null;
            }
            
            String entityIdentifier;
            if (streamRecord.getKeys().containsKey(DYNAMODB_ID_FIELD)) {
                entityIdentifier = streamRecord.getKeys().get(DYNAMODB_ID_FIELD).getS();
            } else {
                logger.error("Cannot get 'ID' from streamRecord for REMOVE operation");
                return null;
            }
            
            String eventName = dynamodDBStreamRecord.getEventName();
            CloudsearchOperation cloudsearchOperation = EventName.valueOf(eventName).cloudsearchOperation;
            AmazonSdfDTO sdf = new AmazonSdfDTO(cloudsearchOperation, entityIdentifier);
            
            logger.debug("cloudsearchOperation={}, entityIdentifier={}",cloudsearchOperation.name(),entityIdentifier);
            if (cloudsearchOperation == cloudsearchOperation.ADD) {
                    Entity entity = getEntity(entityIdentifier);
                    sdf.setFieldsFromEntity(entity);
            }
            return sdf;
        } catch (Exception e) {
            logger.error("",e);
            throw new RuntimeException(e);
        }
    }

    private Entity getEntity(String entityIdentifier) {
        Entity entity = new Entity();
        entity.setId(entityIdentifier);
        String entityUrl = restApiUrl+"/"+entityIdentifier;
        logger.debug("GETing @{}",entityUrl);
        return entity;
    }

//    private Entity extractFullEntity(Map<String, AttributeValue> map) {
//        Entity entity = new Entity();
//        try {
//            ObjectMapper objectMapper = JsonUtils.newJsonParser();
//            objectMapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
//            AttributeValue attributeValue = map.get(DYNAMODB_BODY_FIELD);
//            entity.setBody((ObjectNode)objectMapper.readTree(attributeValue.getS()));
//            entity.setId(map.get(DYNAMODB_ID_FIELD).getS());
//            entity.setCreated(map.get(DYNAMODB_CREATED_FIELD).getS());
//            entity.setModified(map.get(DYNAMODB_MODIFIED_FIELD).getS());
//        } catch (IOException e) {
//            logger.error("",e);
//        }
//        return entity;
//    }

}

