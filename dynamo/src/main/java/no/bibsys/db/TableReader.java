package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonProcessingException;


public class TableReader {

  private final transient TableDriver tableDriver;
  private transient String tableName;



  public TableReader(final TableDriver tableDriver) {
    this.tableDriver = tableDriver;

  }


  public void setTableName(final String tableName){
    if(this.tableName==null){
      this.tableName=tableName;
    }
    else{
      throw new IllegalStateException("Cannot initialize tableName twice");
    }
  }


  public String getEntry(final String id) throws JsonProcessingException {
    final Table table = tableDriver.getDynamoDB().getTable(tableName);
    final Item item = table.getItem("id", id);
    return item.toJSON();
  }

}
