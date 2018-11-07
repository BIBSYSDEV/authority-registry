package no.bibsys.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

import no.bibsys.db.exceptions.ItemExistsException;
import no.bibsys.db.exceptions.NoItemException;

public final class ItemDriver {
    
    private static final Logger logger = LoggerFactory.getLogger(ItemDriver.class);
    private static final String NULL = "null";
    private transient DynamoDB dynamoDb;


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

    public Table getTable(final String tableName) {
        return dynamoDb.getTable(tableName);
    }

    public Optional<String> addItem(String tableName, String itemJson) {
        String id = NULL;
        try {
            Item item = Item.fromJSON(itemJson);

            id = item.asMap().getOrDefault("id", NULL).toString();

            final Table table = dynamoDb.getTable(tableName);

            PutItemSpec putItemSpec = new PutItemSpec()
                    .withItem(item)
                    .withConditionExpression(DynamoConstantsHelper.KEY_NOT_EXISTS);
            table.putItem(putItemSpec);
            
            return Optional.ofNullable(item.toJSON()); 
        } catch (ConditionalCheckFailedException e) {
            logger.warn(String.format("Item with id:%s already exists", id));
            throw new ItemExistsException(String.format("Item with id:%s already exists", id), e);
        }
    }

    public Optional<String> updateItem(String tableName, String itemJson) {
        String id = NULL;
        final Item item = Item.fromJSON(itemJson);
        id = item.asMap().getOrDefault("id", NULL).toString();

        if(getItem(tableName, id).isPresent()) {

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
            logger.warn(String.format("Item with id:%s does not exist", id));
            throw new NoItemException(String.format("Item with id %s does not exist", id));
        }
    }


    public Optional<String> getItem(String tableName, String id) {
        final Table table = dynamoDb.getTable(tableName);
        final Optional<Item> itemOpt = Optional.ofNullable(table.getItem("id", id));
        if(itemOpt.isPresent()) {
            return itemOpt.map(item -> item.toJSON());
        } else {
            logger.warn(String.format("Item with id:%s does not exist", id));
            throw new NoItemException(String.format("Item with id %s does not exist", id));
        }
    }
    
    public void deleteItem(String tableName, String id)  {

        Table table = dynamoDb.getTable(tableName);
        table.deleteItem(new PrimaryKey("id", id));
    }


}
