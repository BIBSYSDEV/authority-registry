package no.bibsys.controllers;

import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.ws.rs.BadRequestException;
import no.bibsys.db.DatabaseManager;
import no.bibsys.handlers.CreateRegistryRequest;
import no.bibsys.handlers.EditRegistryRequest;
import no.bibsys.handlers.EmptyRegistryRequest;
import no.bibsys.responses.PathResponse;
import no.bibsys.responses.SimpleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    private transient final static String contentType="application/json;charset=UTF-8";

    private transient final DatabaseManager databaseManager;

    @Autowired
    public DatabaseController(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }


    @PostMapping(path = "/registry", produces = contentType)
    public SimpleResponse editRegistry(@RequestBody EditRegistryRequest request)
        throws InterruptedException, TableAlreadyExistsException {
        EditRegistryRequest specific = request.specify();
        if (specific instanceof EmptyRegistryRequest) {
            return emptyRegistry((EmptyRegistryRequest) specific);
        } else {
            throw new BadRequestException();
        }

    }

    @PutMapping(path = "/registry/{registryName}", produces = contentType)
    public SimpleResponse putNewRegistry(@PathVariable String registryName,
        @RequestBody String validationSchema) throws InterruptedException, JsonProcessingException {
        return createTable(new CreateRegistryRequest(registryName, validationSchema));


    }


    @PostMapping(path = "/registry/{registryName}", produces = contentType)
    public PathResponse insertEntry(@PathVariable("registryName") String registryName,
        @RequestBody String request) throws IOException {
        databaseManager.insertEntry(registryName, request);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(request);
        String id = node.get("id").asText();
        return new PathResponse(
            String.format("/registry/%s/%s", registryName, id));
    }


    @DeleteMapping(path = "/registry/{registryName}", produces = contentType)
    public SimpleResponse deleteRegistry(@PathVariable String registryName)
        throws InterruptedException {

        databaseManager.deleteRegistry(registryName);
        return new SimpleResponse(
            String.format("Registry %s has been deleted", registryName));
    }


    @RequestMapping(value = "*", method = RequestMethod.GET)
    public SimpleResponse getFallback() {
        return new SimpleResponse("Invalid path");
    }


    private SimpleResponse emptyRegistry(EmptyRegistryRequest request) throws InterruptedException {
        String registryName = request.getRegistryName();
        databaseManager.emptyRegistry(registryName);
        return new SimpleResponse(String.format("Registry %s has been emptied", registryName));
    }

    private SimpleResponse createTable(CreateRegistryRequest request)
        throws InterruptedException, JsonProcessingException {
        String tableName = request.getRegistryName();
        databaseManager.createRegistry(tableName, request.getValidationSchema());
        return new SimpleResponse(
            String.format("A registry with name %s has been created", tableName));
    }


}