package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonProcessingException;


public class TableReader {

  private TableDriver tableDriver;
  private String tableName;



  public TableReader(TableDriver tableDriver) {
    this.tableDriver = tableDriver;

  }


  public void setTableName(String tableName){
    if(this.tableName==null){
      this.tableName=tableName;
    }
    else{
      throw new IllegalStateException("Cannot initialize tableName twice");
    }
  }


  public String getEntry(String id) throws JsonProcessingException {
    Table table = tableDriver.getDynamoDB().getTable(tableName);
    Item item = table.getItem("id", id);
    String json = item.toJSON();
    return json;
  }

}
