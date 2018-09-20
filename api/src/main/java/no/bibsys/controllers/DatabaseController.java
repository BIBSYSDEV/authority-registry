package no.bibsys.controllers;

import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import no.bibsys.db.DatabaseManager;
import no.bibsys.handlers.CreateRegistryRequest;
import no.bibsys.responses.SimpleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RestController
@EnableWebMvc
public class DatabaseController {


    private transient final DatabaseManager databaseManager;

    @Autowired
    public DatabaseController(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @PostMapping(path = "/registry/create", produces = "application/json;charset=UTF-8")
    public SimpleResponse createRegistry(@RequestBody CreateRegistryRequest request)
        throws InterruptedException, TableAlreadyExistsException {
        String tableName = request.getRegistryName();
        databaseManager.createRegistry(tableName);
        return new SimpleResponse(
            String.format("A registry with name %s has been created", tableName));
    }


    @PutMapping(path = "/registry/{regName}/put", produces = "application/json;charset=UTF-8")
    public SimpleResponse insertEntry(@PathVariable("regName") String registryName,
        @RequestBody String request) {
        databaseManager.insert(registryName, request);
        return new SimpleResponse(
            String.format("A new item has been inserted into %s ", registryName));
    }


    @RequestMapping(value = "*", method = RequestMethod.GET)
    public SimpleResponse getFallback() {
        return new SimpleResponse("Invalid path");
    }


}