package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.bibsys.db.exceptions.ItemExistsException;
import no.bibsys.db.structures.Entry;

public class TableWriter {


    private final transient TableDriver tableDriver;
    private final transient ObjectMapper mapper;
    private final transient String tableName;


    public TableWriter(final TableDriver tableDriver, String tableName) {
        this.tableDriver = tableDriver;
        this.tableName = tableName;
        mapper = new ObjectMapper();
    }


    public void insertEntry(final Entry entry) throws JsonProcessingException {
        final String json = mapper.writeValueAsString(entry);
        insertJson(json);
    }


    public void deleteEntry(String id) {
        Table table = tableDriver.getTable(tableName);
        table.deleteItem(new PrimaryKey("id", id));

    }


    public void insertJson(final String json) {
        String id = "null";
        try {
            final Item item = Item.fromJSON(json);
            id = item.asMap().getOrDefault("id", "null").toString();

            final Table table = tableDriver.getTable(tableName);
            PutItemSpec putItemSpec = new PutItemSpec()
                .withItem(item)
                .withConditionExpression(DynamoConstantsHelper.KEY_NOT_EXISTS);
            table.putItem(putItemSpec);
        } catch (ConditionalCheckFailedException e) {
            throw new ItemExistsException(String.format("Item with id:%s already exits", id), e);
        }

    }

}
