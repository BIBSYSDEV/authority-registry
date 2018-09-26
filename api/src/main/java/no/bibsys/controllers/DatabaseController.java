package no.bibsys.controllers;

import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import no.bibsys.db.DatabaseManager;
import no.bibsys.handlers.CreateRegistryRequest;
import no.bibsys.responses.PathResponse;
import no.bibsys.responses.SimpleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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


    @PostMapping(path = "/registry/{registryName}", produces = "application/json;charset=UTF-8")
    public PathResponse insertEntry(@PathVariable("registryName") String registryName,
        @RequestBody String request) throws IOException {
        databaseManager.insert(registryName, request);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(request);
        String id = node.get("id").asText();
        return new PathResponse(
            String.format("/registry/%s/%s", registryName, id));
    }


    @PostMapping(path = "/registry", produces = "application/json;charset=UTF-8")
    public SimpleResponse createRegistry(@RequestBody CreateRegistryRequest request)
        throws InterruptedException, TableAlreadyExistsException {
        String tableName = request.getRegistryName();
        databaseManager.createRegistry(tableName);
        return new SimpleResponse(
            String.format("A registry with name %s has been created", tableName));
    }



    @RequestMapping(value = "*", method = RequestMethod.GET)
    public SimpleResponse getFallback() {
        return new SimpleResponse("Invalid path");
    }


}