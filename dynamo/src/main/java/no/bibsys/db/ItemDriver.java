package no.bibsys.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class ItemDriver {

    private final transient DynamoDB dynamoDb;
    private final static Logger logger = LoggerFactory.getLogger(ItemDriver.class);

    private ItemDriver(final DynamoDB dynamoDb) {
        this.dynamoDb = dynamoDb;
    }

    /**
     * Create custom connection with DynamoDB.
     *
     * @return customized ItemDriver
     */
    public static ItemDriver create(final DynamoDB dynamoDb) {
        ItemDriver tableDriver = new ItemDriver(dynamoDb);
        return tableDriver;
    }

    /**
     * Add item to table
     * @param tableName
     * @param itemJson
     * @return true if item is added
     */
    public boolean addItem(String tableName, String itemId, String itemJson) {

        Map<String, Object> itemMap = new ConcurrentHashMap<>();
        itemMap.put("id", itemId);

        try {
            ObjectMapper objectMapper = JsonUtils.getObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Object> bodyMap = objectMapper.readValue(itemJson, Map.class);
            itemMap.put("body", bodyMap);
        } catch (IllegalArgumentException | IOException e) {
            logger.error("Error mapping json, tableId={}, itemId={}, reason={}", tableName, itemId, e.getMessage());
            return false;
        } 

        Item item = Item.fromMap(itemMap);

        boolean success = false;
        if(!itemId.isEmpty()) {
            try {

                final Table table = dynamoDb.getTable(tableName);

                PutItemSpec putItemSpec = new PutItemSpec()
                        .withItem(item)
                        .withConditionExpression(DynamoConstantsHelper.KEY_NOT_EXISTS);
                table.putItem(putItemSpec);
                success = true;
            } catch (ConditionalCheckFailedException | ResourceNotFoundException e) {
                logger.error("Error adding item, tableId={}, itemId={}, reason={}", tableName, itemId, e.getMessage());
                success = false;
            }
        }
        
        logger.info("Item added successfully, tableId={}, itemId={}", tableName, itemId);

        return success;
    }


    /**
     * Update item already in a table. 
     * @param tableName
     * @param itemJson
     * @return
     */
    public Optional<String> updateItem(String tableName, String itemId, String itemJson) {
        
        if(itemId.isEmpty()) {
            logger.error("ItemId is empty, tableId={}", tableName);
            return Optional.empty();
        }

        if(itemExists(tableName, itemId)) {
            Map<String, Object> itemMap = new ConcurrentHashMap<>();
            itemMap.put("id", itemId);

            try {
                ObjectMapper objectMapper = JsonUtils.getObjectMapper();
                @SuppressWarnings("unchecked")
                Map<String, Object> bodyMap = objectMapper.convertValue(objectMapper.readTree(itemJson), Map.class);
                itemMap.put("body", bodyMap);
            } catch (IllegalArgumentException | IOException e) {
                logger.error("Error mapping json, tableId={}, itemId={}, reason={}", tableName, itemId, e.getMessage());
                return Optional.empty();
            }

            final Item item = Item.fromMap(itemMap);
            final Table table = dynamoDb.getTable(tableName);

            List<AttributeUpdate> updateList = new ArrayList<>();
            item.attributes().forEach(entry -> {
                if(!entry.getKey().equals("id")) {
                    updateList.add(new AttributeUpdate(entry.getKey()).put(entry.getValue()));
                }
            });

            UpdateItemSpec putItemSpec = new UpdateItemSpec()
                    .withPrimaryKey("id", item.get("id"))
                    .withAttributeUpdate(updateList)
                    .withReturnValues(ReturnValue.UPDATED_OLD);
            Item returnItem = table.updateItem(putItemSpec).getItem();
            logger.error("Item updated successfully, tableId={}, itemId={}}", tableName, itemId);
            return Optional.ofNullable(returnItem.toJSON());
        } else {
            logger.error("Can not update non-existing item, tableId={}, itemId={}", tableName, itemId);
            return Optional.empty();
        }
    }


    public Optional<String> getItem(String tableName, String itemId) {
        final Table table = dynamoDb.getTable(tableName);
        try {
            final Optional<Item> itemOpt = Optional.ofNullable(table.getItem("id", itemId));
            ObjectMapper objectMapper = JsonUtils.getObjectMapper();
            logger.error("Item read successfully, tableId={}, itemId={}}", tableName, itemId);
            return Optional.ofNullable(objectMapper.writeValueAsString(itemOpt.get().get("body")));
        }catch(ResourceNotFoundException | NoSuchElementException | JsonProcessingException e) {
            logger.error("Error getting item, tableId={}, itemId={}, reason={}", tableName, itemId, e.getMessage());
            return Optional.empty();
        }
    }

    public boolean deleteItem(String tableName, String itemId)  {

        Table table = dynamoDb.getTable(tableName);
        if(itemExists(tableName, itemId)) {
            table.deleteItem(new PrimaryKey("id", itemId));
            logger.error("Item deleted successfully, tableId={}, itemId={}}", tableName, itemId);
            return true;
        }
        logger.error("Can not delete on-existing item, tableId={}, itemId={}", tableName, itemId);
        return false;
    }

    public boolean itemExists(String tableName, String id) {
        return getItem(tableName, id).isPresent();
    }
}
