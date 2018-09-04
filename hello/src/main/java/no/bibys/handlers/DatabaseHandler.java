package no.bibys.handlers;

import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import java.io.IOException;
import no.bibys.db.TableCreator;
import no.bibys.db.TableDriverFactory;
import no.bibys.db.TableWriter;
import no.bibys.handlers.requests.DatabaseWriteRequest;
import no.bibys.handlers.responses.SimpleResponse;


public class DatabaseHandler extends HandlerHelper<DatabaseWriteRequest, SimpleResponse> implements
    RequestStreamHandler {


  public DatabaseHandler() {
    super(DatabaseWriteRequest.class, SimpleResponse.class);

  }


  @Override
  SimpleResponse processInput(DatabaseWriteRequest input) throws IOException {
    try {
      String tableName = input.getTableName();
      String jsonObject = input.getJsonObject();
      TableCreator tableCreator = TableCreator.create(new TableDriverFactory());
      boolean tableExists = tableCreator.tableExists(tableName);
      if (!tableExists) {
        tableCreator.createTable(tableName);
      }

      TableWriter tableWriter = TableWriter.create(tableName, new TableDriverFactory());
      tableWriter.insertJson(jsonObject);
      return new SimpleResponse("DB works! Go check it!!!!!");
    } catch (InterruptedException e) {
      e.printStackTrace();
      throw new IOException(e);
    }

  }
}