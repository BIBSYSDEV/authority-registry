package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import java.util.Optional;


public class TableReader {

    private final transient TableDriver tableDriver;
    private final transient String tableName;


    public TableReader(final TableDriver tableDriver, String tableName) {
        this.tableDriver = tableDriver;
        this.tableName = tableName;

    }


    public Optional<String> getEntry(final String id) {
        final Table table = tableDriver.getDynamoDb().getTable(tableName);
        final Optional<Item> itemOpt = Optional.ofNullable(table.getItem("id", id));
        return itemOpt.map(item -> item.toJSON());
    }


}
