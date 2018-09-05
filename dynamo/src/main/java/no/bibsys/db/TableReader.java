//package no.bibys.db;

//public class TableReader extends TableDriver {
//
//
//
//
//  public static TableReader create(String tableName){
//    return new TableReader(tableName,factory.build());
//  }
//
//
//  private String tableName;
//
//
//  private TableReader(String tableName, ) {
//    super(tableDriver.getClient(),tableDriver.getDynamoDB());
//
//    this.tableName=tableName;
//  }
//
//
//  public String getEntry(String id) throws JsonProcessingException {
//    Table table = dynamoDB.getTable(tableName);
//    Item item=table.getItem("id",id);
//    String json=item.toJSON();
//    return json;
//  }
//
//}
