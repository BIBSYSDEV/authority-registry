package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.bibsys.db.structures.Entry;

public class TableWriter {


    private final transient TableDriver tableDriver;
    private final transient ObjectMapper mapper;
    private transient String tableName;


    public TableWriter(final TableDriver tableDriver) {
        this.tableDriver = tableDriver;
        mapper = new ObjectMapper();
    }


    public void setTableName(final String tableName) {
        if (this.tableName == null) {
            this.tableName = tableName;
        } else {
            throw new IllegalStateException("Cannot initialize tableName twice");
        }
    }


    public void insertEntry(final Entry entry) throws JsonProcessingException {
        final String json = mapper.writeValueAsString(entry);
        insertJson(json);
    }

    public void insertJson(final String json) {
        final Item item = Item.fromJSON(json);
        final Table table = tableDriver.getTable(tableName);
        table.putItem(item);
    }


}
