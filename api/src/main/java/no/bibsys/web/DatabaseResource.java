package no.bibsys.web;

import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION;
import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION_AWS_PROXY;
import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD;
import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR;
import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE;
import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI;
import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE;
import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import no.bibsys.db.DatabaseManager;
import no.bibsys.db.structures.EntityRegistryTemplate;
import no.bibsys.web.model.PathResponse;
import no.bibsys.web.model.SimpleResponse;

@Path("/registry")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@OpenAPIDefinition(info = 
@Info(
        title = "Entity Registry",
        version = "0.0",
        description = "API documentation for Entity Registry",
        license = @License(name = "MIT", url = "https://opensource.org/licenses/MIT"),
        contact = @Contact(url = "http://example.org", name = "Entity registry team", email = "entity@example.org")
        )
        )
public class DatabaseResource {

    private static final String ENTITY_ID = "entityId";
    private static final String NOT_IMPLEMENTED = "Not implemented";
    private static final String STRING = "string";
    private static final String REGISTRY_NAME = "registryName";
    private transient final DatabaseManager databaseManager;

    public DatabaseResource(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @POST
    @Path("/")
    @Operation(extensions = {@Extension(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION, properties = {
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI,
                    value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_AWS_PROXY),})})
    public SimpleResponse createRegistry(@RequestBody(
            description = "Request object to create registry",
            content = @Content(schema = @Schema(
                    implementation = EntityRegistryTemplate.class))) EntityRegistryTemplate request)
                            throws InterruptedException, TableAlreadyExistsException, JsonProcessingException {
        
        databaseManager.createRegistry(request);
        return new SimpleResponse(String.format("A registry with name %s has been created", request.getId()));
    }

    @GET
    @Path("/")
    @Operation(extensions = {@Extension(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION, properties = {
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI,
                    value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_AWS_PROXY),})})
    public SimpleResponse getRegistryList() throws InterruptedException, TableAlreadyExistsException, JsonProcessingException {
        
        List<String> registryList = databaseManager.getRegistryList();
        ObjectMapper mapper = new ObjectMapper();
        return new SimpleResponse(mapper.writeValueAsString(registryList), 200);
    }

    @GET
    @Path("/{registryName}")
    @Operation(extensions = {@Extension(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION, properties = {
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI,
                    value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_AWS_PROXY),})})
    public SimpleResponse getRegistryMetadata(
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of new registry",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName)
                    throws InterruptedException, IOException {
        
         EntityRegistryTemplate metadata = databaseManager.getRegistryMetadata(registryName);
         ObjectMapper mapper = new ObjectMapper();
        
        return new SimpleResponse(mapper.writeValueAsString(metadata), 200);
    }

    @PUT
    @Path("/{registryName}")
    @Operation(extensions = {@Extension(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION, properties = {
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI,
                    value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_AWS_PROXY),})})
    public SimpleResponse updateRegistryMetadata(
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of new registry",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName,
            @RequestBody(description = "Validation schema",
            content = @Content(schema = @Schema(
                    implementation = EntityRegistryTemplate.class))) EntityRegistryTemplate request)
                            throws InterruptedException, JsonProcessingException {
        databaseManager.updateRegistry(request);
        return new SimpleResponse(String.format("Registry %s has been updated", request.getId()));
    }


    @DELETE
    @Path("/{registryName}")
    @Operation(extensions = {@Extension(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION, properties = {
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI,
                    value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_AWS_PROXY),})})
    public SimpleResponse deleteRegistry(
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to delete",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName)
                    throws InterruptedException {

        databaseManager.deleteRegistry(registryName);
        return new SimpleResponse(String.format("Registry %s has been deleted", registryName));
    }

    @DELETE
    @Path("/{registryName}/empty")
    @Operation(extensions = {@Extension(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION, properties = {
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI,
                    value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_AWS_PROXY),})})
    public SimpleResponse emptyRegistry(
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to delete",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName) throws InterruptedException {
        databaseManager.emptyRegistry(registryName);
        return new SimpleResponse(String.format("Registry %s has been emptied", registryName));
    }

    @GET
    @Path("/{registryName}/schema")
    @Operation(extensions = {@Extension(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION, properties = {
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI,
                    value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_AWS_PROXY),})})
    public SimpleResponse getRegistrySchema(
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to get schema",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName) throws InterruptedException {
        return new SimpleResponse(NOT_IMPLEMENTED, 501);
    }

    @PUT
    @Path("/{registryName}/schema")
    @Operation(extensions = {@Extension(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION, properties = {
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI,
                    value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_AWS_PROXY),})})
    public SimpleResponse updateRegistrySchema(
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to update",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName, 
            @RequestBody(description = "Validation schema",
            content = @Content(schema = @Schema(type = STRING))) String validationSchema
            ) throws InterruptedException {
        return new SimpleResponse(NOT_IMPLEMENTED, 501);
    }


    @POST
    @Path("/{registryName}/entity")
    @Operation(extensions = {@Extension(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION, properties = {
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI,
                    value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_AWS_PROXY),})})
    public PathResponse createEntity(
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to add to",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName,
            @RequestBody(description = "Entity to create",
            content = @Content(schema = @Schema(type = STRING))) String entity)
                    throws IOException {
        databaseManager.addEntry(registryName, entity);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(entity);
        String id = node.get("id").asText();
        return new PathResponse(String.format("/registry/%s/entity/%s", registryName, id));
    }

    @GET
    @Path("/{registryName}/entity")
    @Operation(extensions = {@Extension(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION, properties = {
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI,
                    value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_AWS_PROXY),})})
    public SimpleResponse entitiesSummary(
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to get entity summary from",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName)
                    throws IOException {
        String entityJson = "";
        
        return new SimpleResponse(entityJson);
    }

    @GET
    @Path("/{registryName}/entity/{entityId}")
    @Operation(extensions = {@Extension(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION, properties = {
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI,
                    value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_AWS_PROXY),})})
    public SimpleResponse getEntity(
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to get entity from",
            schema = @Schema(type = STRING)) @PathParam(ENTITY_ID) String registryName, 
            @Parameter(in = ParameterIn.PATH, name = ENTITY_ID, required = true,
            description = "Id of entity to get",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String entityId)
                    throws IOException {
        String entityJson = "";
        
        Optional<String> entry = databaseManager.readEntry(registryName, entityId);
        if(entry.isPresent()) {
            entityJson = entry.get();
        }else {
            return new SimpleResponse(String.format("Entity with id %s not found in %s", entityId, registryName), 404);
        }
        
        return new SimpleResponse(entityJson);
    }

    @DELETE
    @Path("/{registryName}/entity/{entityId}")
    @Operation(extensions = {@Extension(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION, properties = {
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI,
                    value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_AWS_PROXY),})})
    public SimpleResponse deleteEntity(
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to delete entity from",
            schema = @Schema(type = STRING)) @PathParam(ENTITY_ID) String registryName, 
            @Parameter(in = ParameterIn.PATH, name = ENTITY_ID, required = true,
            description = "Id of entity to delete",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String entityId)
                    throws IOException {
        return new SimpleResponse(NOT_IMPLEMENTED, 501);
    }
    
    @PUT
    @Path("/{registryName}/entity/{entityId}")
    @Operation(extensions = {@Extension(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION, properties = {
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI,
                    value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE,
            value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_AWS_PROXY),})})
    public SimpleResponse updateEntity(
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry in which to update entity",
            schema = @Schema(type = STRING)) @PathParam(ENTITY_ID) String registryName, 
            @Parameter(in = ParameterIn.PATH, name = ENTITY_ID, required = true,
            description = "Id of entity to be updated",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String entityId,
            @RequestBody(description = "Entity to create",
            content = @Content(schema = @Schema(type = STRING))) String entity)
                    throws IOException {
        return new SimpleResponse(NOT_IMPLEMENTED, 501);
    }
}
