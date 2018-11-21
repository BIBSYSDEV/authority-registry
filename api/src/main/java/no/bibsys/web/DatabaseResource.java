package no.bibsys.web;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import no.bibsys.db.EntityManager;
import no.bibsys.db.JsonUtils;
import no.bibsys.db.RegistryManager;
import no.bibsys.db.structures.EntityRegistryTemplate;
import no.bibsys.service.RegistryService;
import no.bibsys.web.model.CreatedRegistry;
import no.bibsys.web.model.InsertEntity;
import no.bibsys.web.security.ApiKeyConstants;
import no.bibsys.web.security.Roles;

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
@SecurityScheme(name=ApiKeyConstants.API_KEY, paramName=ApiKeyConstants.API_KEY_PARAM_NAME, type=SecuritySchemeType.APIKEY, in=SecuritySchemeIn.HEADER)
public class DatabaseResource {

    private static final String ENTITY_ID = "entityId";
    private static final String STRING = "string";
    private static final String REGISTRY_NAME = "registryName";
    private transient final RegistryService registryService;
    private transient final RegistryManager registryManager;
    private transient final EntityManager entityManager;
    private transient final ObjectMapper mapper = JsonUtils.getObjectMapper();
    
    public DatabaseResource(RegistryService registryService, RegistryManager registryManager, EntityManager entityManager) {
    	this.registryService = registryService;
        this.entityManager = entityManager;
        this.registryManager = registryManager;
    }

    @POST
    @Path("/")
    @Operation(extensions = {@Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS, 
            value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
            value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
            value = AwsApiGatewayIntegration.AWS_PROXY),})})
    @SecurityRequirement(name=ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN})
    public Response createRegistry(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey, @RequestBody(
            description = "Request object to create registry",
            content = @Content(schema = @Schema(
                    implementation = EntityRegistryTemplate.class))) EntityRegistryTemplate request)
                            throws JsonProcessingException {

    	request.validate();
    	
		CreatedRegistry createdRegistry = registryService.createRegistry(request);
        return Response.ok(createdRegistry).build();
    }

    @GET
    @Path("/")
    @Operation(extensions = {@Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS, 
            value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
            value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
            value = AwsApiGatewayIntegration.AWS_PROXY),})})
    public Response getRegistryList(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey) throws Exception {

        List<String> registryList = registryManager.getRegistries();
        return Response.ok(mapper.writeValueAsString(registryList)).build();
    }


    @GET
    @Path("/{registryName}")
    @Operation(extensions = {@Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS, 
            value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
            value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
            value = AwsApiGatewayIntegration.AWS_PROXY),})})
    @SecurityRequirement(name=ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN, Roles.REGISTRY_ADMIN})
    public Response getRegistryMetadata(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of new registry",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName)
                    throws IOException {

    	registryManager.validateRegistryExists(registryName);
    	
        EntityRegistryTemplate metadata = registryManager.getRegistryMetadata(registryName);
        return Response.ok(mapper.writeValueAsString(metadata)).build();
    }

    @PUT
    @Path("/{registryName}")
    @Operation(extensions = {@Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS, 
            value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
            value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
            value = AwsApiGatewayIntegration.AWS_PROXY),})})
    @SecurityRequirement(name=ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN, Roles.REGISTRY_ADMIN})
    public Response updateRegistryMetadata(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of new registry",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName,
            @RequestBody(description = "Validation schema",
            content = @Content(schema = @Schema(
                    implementation = EntityRegistryTemplate.class))) EntityRegistryTemplate request)
                            throws InterruptedException, JsonProcessingException {

    	registryManager.validateRegistryExists(registryName);
    	
        registryManager.updateRegistryMetadata(request);
        return Response.accepted(String.format("Registry %s has been updated", request.getId())).build();
    }


    @DELETE
    @Path("/{registryName}")
    @Operation(extensions = {@Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS, 
            value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
            value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
            value = AwsApiGatewayIntegration.AWS_PROXY),})})
    @SecurityRequirement(name=ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN, Roles.REGISTRY_ADMIN})
    public Response deleteRegistry(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to delete",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName)
                    throws InterruptedException {

    	registryManager.validateRegistryExists(registryName);

        registryService.deleteRegistry(registryName);
        return Response.ok(String.format("Registry %s has been deleted", registryName)).build();
    }

    @DELETE
    @Path("/{registryName}/empty")
    @Operation(extensions = {@Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS, 
            value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
            value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
            value = AwsApiGatewayIntegration.AWS_PROXY),})})
    @SecurityRequirement(name=ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN, Roles.REGISTRY_ADMIN})
    public Response emptyRegistry(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to delete",
                schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName) {

    	registryManager.validateRegistryExists(registryName);

        registryManager.emptyRegistry(registryName);
        return Response.ok(String.format("Registry %s has been emptied", registryName)).build();
    }

    @GET
    @Path("/{registryName}/schema")
    @Operation(extensions = {@Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS, 
            value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
            value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
            value = AwsApiGatewayIntegration.AWS_PROXY),})})
    @SecurityRequirement(name=ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN, Roles.REGISTRY_ADMIN})
    public Response getRegistrySchema(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to get schema",
                schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName)
        throws IOException {

    	registryManager.validateRegistryExists(registryName);

        Optional<String> schemaAsJson = registryManager.getSchemaAsJson(registryName);
        return Response.ok(schemaAsJson.get()).build();
    }

    @PUT
    @Path("/{registryName}/schema")
    @Operation(extensions = {@Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS, 
            value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
            value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
            value = AwsApiGatewayIntegration.AWS_PROXY),})})
    @SecurityRequirement(name=ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN, Roles.REGISTRY_ADMIN})
    public Response updateRegistrySchema(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to update",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName, 
            @RequestBody(description = "Validation schema",
            content = @Content(schema = @Schema(type = STRING))) String validationSchema
    ) throws IOException {

    	registryManager.validateRegistryExists(registryName);

        registryManager.setSchemaJson(registryName, validationSchema);
        return Response.ok(String.format("/registry/%s/schema", registryName)).build();
    }


    @POST
    @Path("/{registryName}/entity")
    @Operation(extensions = {@Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS, 
            value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
            value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
            value = AwsApiGatewayIntegration.AWS_PROXY),})})
    @SecurityRequirement(name=ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN, Roles.REGISTRY_ADMIN})
    public Response createEntity(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to add to",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName,
            @RequestBody(description = "Entity to create",
            content = @Content(schema = @Schema(type = STRING))) String entity)
                    throws IOException {

    	registryManager.validateRegistryExists(registryName);

        Optional<String> entityId = entityManager.addEntity(registryName, entity);

        return Response.ok(new InsertEntity(String.format("/registry/%s/entity/%s", registryName, entityId.get()), entityId.get())).build();
    }

    @GET
    @Path("/{registryName}/entity")
    @Operation(extensions = {@Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS, 
            value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
            value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
            value = AwsApiGatewayIntegration.AWS_PROXY),})})
    public Response entitiesSummary(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to get entity summary from",
                schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName) {

    	registryManager.validateRegistryExists(registryName);

        return Response.status(Status.NOT_IMPLEMENTED).entity("Not implemented").build();
    }

    @GET
    @Path("/{registryName}/entity/{entityId}")
    @Operation(extensions = {@Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS, 
            value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
            value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
            value = AwsApiGatewayIntegration.AWS_PROXY),})})
    @SecurityRequirement(name=ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN, Roles.REGISTRY_ADMIN})
    public Response getEntity(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to get entity from",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName, 
            @Parameter(in = ParameterIn.PATH, name = ENTITY_ID, required = true,
            description = "Id of entity to get",
                schema = @Schema(type = STRING)) @PathParam(ENTITY_ID) String entityId) {

    	registryManager.validateRegistryExists(registryName);
    	entityManager.validateItemExists(registryName, entityId);
    	
        Optional<String> entity = entityManager.getEntity(registryName, entityId);
        return Response.ok(entity.get()).build();
    }

    @DELETE
    @Path("/{registryName}/entity/{entityId}")
    @Operation(extensions = {@Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS, 
            value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
            value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
            value = AwsApiGatewayIntegration.AWS_PROXY),})})
    @SecurityRequirement(name=ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN})
    public Response deleteEntity(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to delete entity from",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName, 
            @Parameter(in = ParameterIn.PATH, name = ENTITY_ID, required = true,
            description = "Id of entity to delete",
                schema = @Schema(type = STRING)) @PathParam(ENTITY_ID) String entityId) {
    	
    	registryManager.validateRegistryExists(registryName);
    	entityManager.validateItemExists(registryName, entityId);

        entityManager.deleteEntity(registryName, entityId);

        return Response.ok(String.format("Entity with id %s is deleted from %s", entityId, registryName)).build();
    }

    @PUT
    @Path("/{registryName}/entity/{entityId}")
    @Operation(extensions = {@Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS, 
            value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue=true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
            value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD,
            value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
            value = AwsApiGatewayIntegration.AWS_PROXY),})})
    @SecurityRequirement(name=ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN, Roles.REGISTRY_ADMIN})
    public Response updateEntity(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry in which to update entity",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName, 
            @Parameter(in = ParameterIn.PATH, name = ENTITY_ID, required = true,
            description = "Id of entity to be updated",
            schema = @Schema(type = STRING)) @PathParam(ENTITY_ID) String entityId, 
            @RequestBody(description = "Entity to update",
                content = @Content(schema = @Schema(type = STRING))) String entity) {

    	registryManager.validateRegistryExists(registryName);
    	entityManager.validateItemExists(registryName, entityId);

        entityManager.updateEntity(registryName, entityId, entity);

        return Response.ok(String.format("Entity with id %s in %s has been updated", entityId, registryName)).build();
    }
}
