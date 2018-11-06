package no.bibsys.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.bibsys.db.exceptions.ItemExistsException;
import no.bibsys.db.exceptions.NoItemException;
import no.bibsys.db.structures.Entry;

public class EntityManager {


    private static final String NULL = "null";
    private final transient TableDriver tableDriver;
    private final transient ObjectMapper mapper;
    private final transient String tableName;


    public EntityManager(final TableDriver tableDriver, String tableName) {
        this.tableDriver = tableDriver;
        this.tableName = tableName;
        mapper = new ObjectMapper();
    }

    public Optional<String> getEntry(final String id) {
        final Table table = tableDriver.getDynamoDb().getTable(tableName);
        final Optional<Item> itemOpt = Optional.ofNullable(table.getItem("id", id));
        return itemOpt.map(item -> item.toJSON());
    }

    public void deleteEntry(String id)  {

        Table table = tableDriver.getTable(tableName);
        table.deleteItem(new PrimaryKey("id", id));
    }

    public void addEntry(final Entry entry) throws JsonProcessingException {
        final String json = mapper.writeValueAsString(entry);
        addJson(json);
    }

    public void addJson(final String json) throws JsonProcessingException {
        String id = NULL;
        try {
            Item item = Item.fromJSON(json);

            id = item.asMap().getOrDefault("id", NULL).toString();

            final Table table = tableDriver.getTable(tableName);

            PutItemSpec putItemSpec = new PutItemSpec()
                    .withItem(item)
                    .withConditionExpression(DynamoConstantsHelper.KEY_NOT_EXISTS);
            table.putItem(putItemSpec);
        } catch (ConditionalCheckFailedException e) {
            throw new ItemExistsException(String.format("Item with id:%s already exists", id), e);
        }

    }

    public void updateEntry(final Entry entry) throws JsonProcessingException {
        final String json = mapper.writeValueAsString(entry);
        updateJson(json);
    }

    public String updateJson(final String json) {
        String id = NULL;
        final Item item = Item.fromJSON(json);
        id = item.asMap().getOrDefault("id", NULL).toString();

        if(getEntry(id).isPresent()) {

            final Table table = tableDriver.getTable(tableName);

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
            return returnItem.toJSON();
        } else {
            throw new NoItemException("Item with id %s does not exist");
        }
    }
}
