package no.bibys.handlers;

import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import java.io.IOException;
import no.bibsys.db.TableCreator;
import no.bibys.handlers.requests.DatabaseWriteRequest;
import no.bibys.handlers.responses.SimpleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DatabaseHandler extends HandlerHelper<DatabaseWriteRequest, SimpleResponse> implements
    RequestStreamHandler {


//  @Autowired
//  private  TableWriter tableWriter;
  @Autowired
  private  TableCreator tableCreator;


  public DatabaseHandler() {
    super(DatabaseWriteRequest.class, SimpleResponse.class);

  }


  @Override
  SimpleResponse processInput(DatabaseWriteRequest input) throws IOException {
    try {
      String tableName = input.getTableName();
      String jsonObject = input.getJsonObject();
      boolean tableExists = tableCreator.tableExists(tableName);
      if (!tableExists) {
        tableCreator.createTable(tableName);
      }

//      tableWriter.insertJson(jsonObject);
      return new SimpleResponse("DB works! Go check it!!!!!");
    } catch (InterruptedException e) {
      e.printStackTrace();
      throw new IOException(e);
    }

  }
}