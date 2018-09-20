package no.bibsys.db;


import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.amazonaws.services.dynamodbv2.model.TableNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseManager {

    private TableDriver tableDriver;

    @Autowired
    public DatabaseManager(TableDriver tableDriver) {
        this.tableDriver = tableDriver;
    }


    public void createRegistry(String tableName)
        throws InterruptedException, TableAlreadyExistsException {
        TableCreator tableCreator = new TableCreator(tableDriver);
        if (!registryExists(tableName)) {
            tableCreator.createTable(tableName);
        } else {
            throw new TableAlreadyExistsException(
                String.format("Registry %s already exists", tableName));
        }
    }


    public void insert(String tableName, String json) {
        if (registryExists(tableName)) {
            TableWriter tableWriter = new TableWriter(tableDriver, tableName);
            tableWriter.insertJson(json);
        } else {
            throw new TableNotFoundException(
                String.format("Registry %s does not exist", tableName));
        }

    }

    public String readEntry(String tableName, String id) {
        if (registryExists(tableName)) {
            TableReader tableReader = new TableReader(tableDriver, tableName);
            return tableReader.getEntry(id);
        } else {
            throw new TableNotFoundException(
                String.format("Registry %s does not exist", tableName));
        }

    }




    boolean registryExists(String tableName) {

        return new TableCreator(tableDriver).tableExists(tableName);
    }


}
