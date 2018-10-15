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
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import no.bibsys.web.model.CreateRegistryRequest;
import no.bibsys.web.model.EditRegistryRequest;
import no.bibsys.web.model.EmptyRegistryRequest;
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
    public SimpleResponse editRegistry(@RequestBody(
            description = "Request object to edit existing registry",
            content = @Content(schema = @Schema(
                    implementation = EditRegistryRequest.class))) EditRegistryRequest request)
            throws InterruptedException, TableAlreadyExistsException {
        EditRegistryRequest specific = request.specify();
        if (specific instanceof EmptyRegistryRequest) {
            return emptyRegistry((EmptyRegistryRequest) specific);
        } else {
            throw new BadRequestException();
        }

    }

    @PUT
    @Path("/{registryName}")
    @Operation(extensions = {@Extension(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION, properties = {
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI,
                    value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR,
                    value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD,
                    value = HttpMethod.PUT),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE,
                    value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_AWS_PROXY),})})
    public SimpleResponse putNewRegistry(
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                    description = "Name of new registry",
                    schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName,
            @RequestBody(description = "Validation schema",
                    content = @Content(schema = @Schema(type = STRING))) String validationSchema)
            throws InterruptedException, JsonProcessingException {
        return createTable(new CreateRegistryRequest(registryName, validationSchema));

    }


    @POST
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
    public PathResponse insertEntry(
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                    description = "Name of registry to insert entity into",
                    schema = @Schema(type = "string")) @PathParam(REGISTRY_NAME) String registryName,
            @RequestBody(description = "Entity to insert",
                    content = @Content(schema = @Schema(type = STRING))) String entity)
            throws IOException {
        databaseManager.insertEntry(registryName, entity);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(entity);
        String id = node.get("id").asText();
        return new PathResponse(String.format("/registry/%s/%s", registryName, id));
    }


    @DELETE
    @Path("/{registryName}")
    @Operation(extensions = {@Extension(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION, properties = {
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI,
                    value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR,
                    value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH),
            @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD,
                    value = HttpMethod.DELETE),
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