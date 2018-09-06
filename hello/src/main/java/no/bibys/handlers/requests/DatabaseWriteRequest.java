package no.bibys.handlers.requests;

import com.google.common.base.Preconditions;

public class DatabaseWriteRequest implements JsonSerializable {


  private String tableName;
  private String jsonObject;




  public DatabaseWriteRequest(String tableName,String jsonObject){
    this.tableName=tableName;
    this.jsonObject=jsonObject;
    Preconditions.checkNotNull(tableName);
    Preconditions.checkNotNull(jsonObject);
  }

  public DatabaseWriteRequest(){};


  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getJsonObject() {
    return jsonObject;
  }

  public void setJsonObject(String jsonObject) {
    this.jsonObject = jsonObject;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DatabaseWriteRequest)) {
      return false;
    }

    DatabaseWriteRequest that = (DatabaseWriteRequest) o;

    if (!tableName.equals(that.tableName)) {
      return false;
    }
    return jsonObject.equals(that.jsonObject);
  }

  @Override
  public int hashCode() {
    int result = tableName.hashCode();
    result = 31 * result + jsonObject.hashCode();
    return result;
  }




}
