package no.bibsys.db;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.amazonaws.services.dynamodbv2.model.TableNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;

import no.bibsys.db.structures.EntityRegistryTemplate;
import no.bibsys.web.model.EditRegistryRequest;

public class DatabaseManager {

    private final transient TableDriver tableDriver;
    private static final transient Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    public DatabaseManager(TableDriver tableDriver) {
        this.tableDriver = tableDriver;
    }

    public void createRegistry(EditRegistryRequest request)
            throws InterruptedException, TableAlreadyExistsException, JsonProcessingException {
        TableManager tableManager = new TableManager(tableDriver);

        String tableName = request.getRegistryName();
        if (registryExists(tableName)) {
            throw new TableAlreadyExistsException(
                    String.format("Registry %s already exists", tableName));
        } else {
            
            EntityRegistryTemplate template = new EntityRegistryTemplate();
            template.setId(tableName);
            request.parseEditRegistryRequest(template.getMetadata());
            
            tableManager.createRegistry(template);
        }
    }
    
    
    public void addEntry(String tableName, String json) {
        if (registryExists(tableName)) {
            TableWriter tableWriter = new TableWriter(tableDriver, tableName);
            tableWriter.addJson(json);
        } else {
            throw new TableNotFoundException(
                    String.format("Registry %s does not exist", tableName));
        }

    }

    public Optional<String> readEntry(String tableName, String id) {
        if (registryExists(tableName)) {
            TableReader tableReader = new TableReader(tableDriver, tableName);
            return tableReader.getEntry(id);
        } else {
            throw new TableNotFoundException(
                    String.format("Registry %s does not exist", tableName));
        }

    }


    public boolean registryExists(String tableName) {
        return new TableManager(tableDriver).tableExists(tableName);
    }


    public void emptyRegistry(String tableName) throws InterruptedException {
        TableManager tableManager = new TableManager(tableDriver);
        tableManager.emptyTable(tableName);

    }

    public void deleteRegistry(String tableName) {
        TableManager tableManager = new TableManager(tableDriver);
        logger.debug(String.format("Deleting table %s", tableName));
        if (registryExists(tableName)) {
            tableManager.deleteTable(tableName);
            logger.debug(String.format("Table %s deleted", tableName));
        } else {
            throw new TableNotFoundException(String.format("Registry %s does not exist", tableName));
        }
    }

}
