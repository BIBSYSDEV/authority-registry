package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.bibsys.db.structures.Entry;

public class TableWriter {


  private TableDriver tableDriver;

  private String tableName;
  private ObjectMapper mapper;


  public TableWriter(TableDriver tableDriver) {
    this.tableDriver = tableDriver;
    mapper = new ObjectMapper();
  }


  public void setTableName(String tableName) {
    if (this.tableName == null) {
      this.tableName = tableName;
    } else {
      throw new IllegalStateException("Cannot initialize tableName twice");
    }
  }


  public void insertEntry(Entry entry) throws JsonProcessingException {
    String json = mapper.writeValueAsString(entry);
    insertJson(json);
  }

  public void insertJson(String json) {
    Item item = Item.fromJSON(json);
    Table table = tableDriver.getTable(tableName);
    table.putItem(item);
  }


}
