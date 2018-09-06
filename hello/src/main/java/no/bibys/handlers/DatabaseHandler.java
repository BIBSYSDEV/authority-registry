package no.bibys.handlers;

import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import java.io.IOException;
import no.bibsys.db.TableCreator;
import no.bibsys.db.TableWriter;
import no.bibys.handlers.requests.DatabaseWriteRequest;
import no.bibys.handlers.responses.SimpleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DatabaseHandler extends HandlerHelper<DatabaseWriteRequest, SimpleResponse> implements
    RequestStreamHandler {


  private  TableCreator tableCreator;
  private  TableWriter tableWriter;
  @Autowired
  private String helloString;



  public DatabaseHandler() {
    super(DatabaseWriteRequest.class, SimpleResponse.class);
  }


  @Autowired
  public void setTableCreator(TableCreator tableCreator){
    this.tableCreator=tableCreator;
  }

  @Autowired
  public void setTableWriter(TableWriter tableWriter){
    this.tableWriter=tableWriter;
  }


//  @Autowired
//  public void setHelloString(String helloString){
//    this.helloString=helloString;
//  }


  public String getHelloString(){
    return this.helloString;
  }



  @Override
  public SimpleResponse processInput(DatabaseWriteRequest input) throws IOException {
    try {
      String tableName = input.getTableName();
      String jsonObject = input.getJsonObject();
      boolean tableExists = tableCreator.tableExists(tableName);
      if (!tableExists) {
        tableCreator.createTable(tableName);
      }
//      tableWriter.setTableName(tableName);
//      tableWriter.insertJson(jsonObject);

      return new SimpleResponse(String.format("DB works! Go check table %s! %s",tableName,
          helloString));
    } catch (Exception e) {
      e.printStackTrace();
      throw new IOException(e);
    }

  }




  public TableCreator getTableCreator() {
    return tableCreator;
  }

  public TableWriter getTableWriter() {
    return tableWriter;
  }
}