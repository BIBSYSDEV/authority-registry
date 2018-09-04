package no.bibys.db;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.bibys.db.structures.Entry;

public class TableWriter extends TableDriver  {


  private String tableName;
  private ObjectMapper mapper;


  public static TableWriter create(String tableName,TableDriverFactory factory){
    return new TableWriter(tableName,factory.build());
  }



  protected TableWriter(String tableName, TableDriver tableDriver) {
     super(tableDriver.getClient(),tableDriver.getDynamoDB());
    this.tableName = tableName;
    mapper=new ObjectMapper();

  }

  public void insertEntry(Entry entry) throws JsonProcessingException {
    String json = mapper.writeValueAsString(entry);
    insertJson(json);
  }

  public void insertJson(String json) {
    Item item = Item.fromJSON(json);
    Table table = getTable(tableName);
    table.putItem(item);
  }




}