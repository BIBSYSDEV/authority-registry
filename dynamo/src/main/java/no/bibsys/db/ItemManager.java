package no.bibsys.db;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ItemManager {

    private final transient ItemDriver itemDriver;

    public ItemManager(final ItemDriver itemDriver) {
        this.itemDriver = itemDriver;
    }

    public Optional<String> getItem(final String tableName, final String id) {
        return itemDriver.getItem(tableName, id);
    }

    public void deleteEntry(final String tableName, final String id)  {
        itemDriver.deleteItem(tableName, id);
    }

    public Optional<String> addJson(final String tableName, final String json) throws JsonProcessingException {
        return itemDriver.addItem(tableName, json);
    }

    public Optional<String> updateJson(final String tableName, final String json) {
        return itemDriver.updateItem(tableName, json);
    }
}
