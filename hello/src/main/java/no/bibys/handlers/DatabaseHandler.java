package no.bibys.handlers;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import java.io.IOException;
import java.util.HashMap;
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
  public DatabaseHandler(TableCreator tableCreator, TableWriter tableWriter) {
    super(DatabaseWriteRequest.class, SimpleResponse.class);
  }


  @Override
  SimpleResponse processInput(DatabaseWriteRequest input) throws IOException {
    try {
      String tableName = input.getTableName();
      HashMap<String,Object> jsonMap=new HashMap<>();
      jsonMap.put("name","orestis");
      jsonMap.put("id",1);
      jsonMap.put("message","hello!");
      Item item= Item.fromMap(jsonMap);
      String jsonObject=item.toJSON();

      //      String jsonObject = input.getJsonObject();
      boolean tableExists = tableCreator.tableExists(tableName);
      if (!tableExists) {
        tableCreator.createTable(tableName);
      }
      tableWriter.insertJson(jsonObject);
      return new SimpleResponse("DB works! Go check it!!!!!");
    } catch (InterruptedException e) {
      e.printStackTrace();
      throw new IOException(e);
    }

  }
}