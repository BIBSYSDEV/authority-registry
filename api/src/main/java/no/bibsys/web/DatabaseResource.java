package no.bibsys.web;

import java.io.IOException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.bibsys.db.DatabaseManager;
import no.bibsys.web.model.EditRegistryRequest;
import no.bibsys.web.model.PathResponse;
import no.bibsys.web.model.SimpleResponse;

@Path("/registry")
public class DatabaseResource {

    private static final String REGISTRY_NAME = "registryName";

    private transient final static String CONTENT_TYPE = "application/json;charset=UTF-8";

    private transient final DatabaseManager databaseManager;

    public DatabaseResource(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @POST
    @Path("/")
    @Produces(CONTENT_TYPE)
    public SimpleResponse createRegistry(EditRegistryRequest request)
            throws InterruptedException, TableAlreadyExistsException {
        throw new BadRequestException();
    }

    @PUT
    @Path("/{registryName}")
    @Produces(CONTENT_TYPE)
    public SimpleResponse putNewRegistry(@PathParam(REGISTRY_NAME) String registryName,
            String validationSchema) throws InterruptedException, JsonProcessingException {
        return createTable(new EditRegistryRequest(registryName));

    }


    @POST
    @Path("/{registryName}")
    @Produces(CONTENT_TYPE)
    public PathResponse insertEntry(@PathParam(REGISTRY_NAME) String registryName, String request)
            throws IOException {
        databaseManager.addEntry(registryName, request);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(request);
        String id = node.get("id").asText();
        return new PathResponse(String.format("/registry/%s/%s", registryName, id));
    }


    @DELETE
    @Path("/{registryName}")
    @Produces(CONTENT_TYPE)
    public SimpleResponse deleteRegistry(@PathParam(REGISTRY_NAME) String registryName)
            throws InterruptedException {

        databaseManager.deleteRegistry(registryName);
        return new SimpleResponse(String.format("Registry %s has been deleted", registryName));
    }

    @DELETE
    @Path("/{registryName}/empty")
    @Produces(CONTENT_TYPE)
    public SimpleResponse emptyRegistry(@PathParam(REGISTRY_NAME) String registryName)
            throws InterruptedException {
        
        databaseManager.emptyRegistry(registryName);
        return new SimpleResponse(String.format("Registry %s has been emptied", registryName));
    }

    private SimpleResponse createTable(EditRegistryRequest request)
            throws InterruptedException, JsonProcessingException {
        String tableName = request.getRegistryName();
        databaseManager.createRegistry(request);
        return new SimpleResponse(
                String.format("A registry with name %s has been created", tableName));
    }


}
