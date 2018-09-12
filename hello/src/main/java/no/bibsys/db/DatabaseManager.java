package no.bibsys.db;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseManager {


  private TableCreator tableCreator;

  @Autowired
  public DatabaseManager(TableCreator tableCreator) {
    this.tableCreator = tableCreator;
  }


  public void createRegistry(String tableName) throws InterruptedException {
    tableCreator.createTable(tableName);
  }


}
