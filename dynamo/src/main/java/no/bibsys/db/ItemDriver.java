package no.bibsys.db;

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

import no.bibsys.db.exceptions.EntityNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ItemDriver {

    private transient final DynamoDB dynamoDb;


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
            Map<String, Object> bodyMap = objectMapper.readValue(itemJson, Map.class);
            itemMap.put("body", bodyMap);
        } catch (IllegalArgumentException | IOException e1) {
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
                success = false;
            }
        }

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
            return Optional.empty();
        }

        if(itemExists(tableName, itemId)) {
            Map<String, Object> itemMap = new ConcurrentHashMap<>();
            itemMap.put("id", itemId);

            try {
                ObjectMapper objectMapper = JsonUtils.getObjectMapper();
                Map<String, Object> bodyMap = objectMapper.convertValue(objectMapper.readTree(itemJson), Map.class);
                itemMap.put("body", bodyMap);
            } catch (IllegalArgumentException | IOException e1) {
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
            return Optional.ofNullable(returnItem.toJSON());
        } else {
            return Optional.empty();
        }
    }


    public Optional<String> getItem(String tableName, String id) {
        final Table table = dynamoDb.getTable(tableName);
        try {
            final Optional<Item> itemOpt = Optional.ofNullable(table.getItem("id", id));
            ObjectMapper objectMapper = JsonUtils.getObjectMapper();
            return Optional.ofNullable(objectMapper.writeValueAsString(itemOpt.get().get("body")));
        }catch(ResourceNotFoundException | NoSuchElementException | JsonProcessingException e) {
            return Optional.empty();
        }
    }

    public boolean deleteItem(String tableName, String id)  {

        Table table = dynamoDb.getTable(tableName);
        if(itemExists(tableName, id)) {
            table.deleteItem(new PrimaryKey("id", id));
            return true;
        }

        return false;
    }

    public boolean itemExists(String tableName, String id) {

        return getItem(tableName, id).isPresent();
    }
}
