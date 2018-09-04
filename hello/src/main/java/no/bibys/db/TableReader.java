package no.bibys.db;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonProcessingException;


public class TableReader extends TableDriver {


  public static TableReader create(String tableName,TableDriverFactory factory){
    return new TableReader(tableName,factory.build());
  }


  private String tableName;


  private TableReader(String tableName, TableDriver tableDriver) {
    super(tableDriver.getClient(),tableDriver.getDynamoDB());

    this.tableName=tableName;
  }


  public String getEntry(String id) throws JsonProcessingException {
    Table table = dynamoDB.getTable(tableName);
    Item item=table.getItem("id",id);
    String json=item.toJSON();
    return json;
  }

}
