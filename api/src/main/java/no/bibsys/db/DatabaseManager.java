package no.bibsys.db;


import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseManager {


    private TableCreator tableCreator;


    @Autowired
    public DatabaseManager(TableCreator tableCreator) {
        this.tableCreator = tableCreator;
    }


    public void createRegistry(String tableName)
        throws InterruptedException, TableAlreadyExistsException {
        if (!registryExists(tableName)) {
            tableCreator.createTable(tableName);
        } else {
            throw new TableAlreadyExistsException(
                String.format("Registry %s already exists", tableName));
        }
    }


    boolean registryExists(String tableName) {
        return tableCreator.tableExists(tableName);
    }


}
