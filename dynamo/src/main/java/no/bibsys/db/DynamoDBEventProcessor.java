package no.bibsys.db;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.StreamRecord;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import no.bibsys.db.AmazonSdfDTO.CloudsearchOperation;
import no.bibsys.db.AmazonSdfDTO.EventName;
import no.bibsys.utils.IoUtils;
import no.bibsys.utils.JsonUtils;

public class DynamoDBEventProcessor implements RequestHandler<DynamodbEvent, Void> {

    //    private static final String DYNAMODB_MODIFIED_FIELD = "modified";
    //    private static final String DYNAMODB_CREATED_FIELD = "created";
    private static final String DYNAMODB_BODY_FIELD = "body";
    private static final String CONTENT_TYPE = "application/ld+json";
    private static final String CONTENT_TYPE_PROPERTY_NAME = "Accept";

    private static final String DYNAMODB_ID_FIELD = "id";
//    private static final String RESTAPI_URL = "RESTAPI_URL";
//    private static final String STAGE = "STAGE";

    private final transient CloudsearchDocumentClient cloudsearchDocumentClient;
    //    private final transient String restApiUrl;

    private static final Logger logger = LoggerFactory.getLogger(DynamoDBEventProcessor.class);
    private static final String ENTITY_ID_FIELD = "id";

    public DynamoDBEventProcessor() {
        cloudsearchDocumentClient = new CloudsearchDocumentClient();
    }

    public DynamoDBEventProcessor(CloudsearchDocumentClient cloudsearchClient) {
        // For mocking
        this.cloudsearchDocumentClient = cloudsearchClient;
        //        restApiUrl = "http://localhost";
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

            String entityUuid;
            if (streamRecord.getKeys().containsKey(DYNAMODB_ID_FIELD)) {
                entityUuid = streamRecord.getKeys().get(DYNAMODB_ID_FIELD).getS();
            } else {
                logger.error("Cannot get 'ID' from streamRecord for REMOVE operation");
                return null;
            }

            String eventName = dynamodDBStreamRecord.getEventName();
            CloudsearchOperation cloudsearchOperation = EventName.valueOf(eventName).cloudsearchOperation;

            logger.debug("cloudsearchOperation={}, entityIdentifier={}",cloudsearchOperation.name(),entityUuid);
//            if (cloudsearchOperation == CloudsearchOperation.ADD) {
                String entityIdentifier = getEntityIdentifier(streamRecord.getNewImage());
                String entitySource = getEntityAsString(entityIdentifier);
                
                ObjectMapper objectMapper = JsonUtils.newJsonParser();
                objectMapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
                ObjectNode objectNode = (ObjectNode)objectMapper.readTree(entitySource);
                Iterator<Entry<String, JsonNode>> fields = objectNode.fields();
                
                AmazonSdfDTO sdf = new AmazonSdfDTO(cloudsearchOperation, entityUuid);
                fields.forEachRemaining(e ->  sdf.setField(e.getKey(), e.getValue().asText()));
                sdf.setField(AmazonSdfDTO.CLOUDSEARCH_PRESENTAION_FIELD, entitySource);
                return sdf;
//            }
        } catch (Exception e) {
            logger.error("",e);
            throw new RuntimeException(e);
        }
    }

    protected String getEntityAsString(String entityIdentifierUrl) {

        logger.debug("entityUrlString={}",entityIdentifierUrl);
        URL entityUrl;
        try {
            entityUrl = new URL(entityIdentifierUrl);

            HttpURLConnection connection = (HttpURLConnection) entityUrl.openConnection();

            // just want to do an HTTP GET here
            connection.setRequestMethod(HttpMethod.GET.name());
            connection.setRequestProperty(CONTENT_TYPE_PROPERTY_NAME, CONTENT_TYPE);

            // give it 15 seconds to respond
            connection.setReadTimeout(15*1000);
            connection.connect();

            // read the output from the server
            String  content  = IoUtils.streamToString(connection.getInputStream());

            logger.debug("content={}", content);

//            ObjectMapper objectMapper = JsonUtils.newJsonParser();
//            objectMapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
//            entity.setBody((ObjectNode)objectMapper.readTree(content));
//            return entity;
            return content;
        } catch (IOException e) {
            logger.error("",e);
            throw new RuntimeException(e);
        }
    }

    private String getEntityIdentifier(Map<String, AttributeValue> map) {
        try {
            ObjectMapper objectMapper = JsonUtils.newJsonParser();
            objectMapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            AttributeValue attributeValue = map.get(DYNAMODB_BODY_FIELD);
            ObjectNode body = (ObjectNode)objectMapper.readTree(attributeValue.getS());
            return body.get(ENTITY_ID_FIELD).asText();
        } catch (IOException e) {
            logger.error("",e);
            throw new RuntimeException(e);
        }
    }

}

