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
import no.bibsys.web.model.SimpleResponse;
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

    private static final String ENTITY_DOES_NOT_EXIST = "Entity with id %s does not exist in registry %s";
    private static final String REGISTRY_DOES_NOT_EXIST = "Registry with name %s does not exist";
    private static final String ENTITY_ID = "entityId";
    private static final String STRING = "string";
    private static final String REGISTRY_NAME = "registryName";
    private transient final RegistryManager registryManager;
    private transient final EntityManager entityManager;

    public DatabaseResource(RegistryManager registryManager, EntityManager entityManager) {
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
    public SimpleResponse createRegistry(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey, @RequestBody(
            description = "Request object to create registry",
            content = @Content(schema = @Schema(
                    implementation = EntityRegistryTemplate.class))) EntityRegistryTemplate request)
                            throws Exception {

        SimpleResponse response = new SimpleResponse();
        if(request.getId() == null||request.getId().isEmpty()) {
            response = new SimpleResponse("Registry create request is missing identifier", Status.BAD_REQUEST);
        }else {

            if(registryManager.registryExists(request.getId())) {
                response = new SimpleResponse(String.format("A registry with name %s already exists", request.getId()), Status.CONFLICT);
            }else {

                registryManager.createRegistryFromTemplate(request);
                response = new SimpleResponse(String.format("A registry with name %s has been created", request.getId()));
            }
        }

        return response;
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
    public SimpleResponse getRegistryList(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey) throws Exception {

        List<String> registryList = registryManager.getRegistries();
        ObjectMapper mapper = JsonUtils.getObjectMapper();
        return new SimpleResponse(mapper.writeValueAsString(registryList), Status.OK);
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
    public SimpleResponse getRegistryMetadata(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of new registry",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName)
                    throws Exception {

        if(!registryManager.registryExists(registryName)) {
            return new SimpleResponse(String.format(REGISTRY_DOES_NOT_EXIST, registryName), Status.NOT_FOUND);
        }

        EntityRegistryTemplate metadata = registryManager.getRegistryMetadata(registryName);
        ObjectMapper mapper = new ObjectMapper();

        return new SimpleResponse(mapper.writeValueAsString(metadata));
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
    public SimpleResponse updateRegistryMetadata(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of new registry",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName,
            @RequestBody(description = "Validation schema",
            content = @Content(schema = @Schema(
                    implementation = EntityRegistryTemplate.class))) EntityRegistryTemplate request)
                            throws Exception {

        if(!registryManager.registryExists(registryName)) {
            return new SimpleResponse(String.format(REGISTRY_DOES_NOT_EXIST, registryName), Status.NOT_FOUND);
        }

        registryManager.updateRegistryMetadata(request);
        return new SimpleResponse(String.format("Registry %s has been updated", request.getId()), Status.ACCEPTED);
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
    public SimpleResponse deleteRegistry(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to delete",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName)
                    throws Exception {

        if(!registryManager.registryExists(registryName)) {
            return new SimpleResponse(String.format(REGISTRY_DOES_NOT_EXIST, registryName), Status.NOT_FOUND);
        }

        if(registryManager.registrySize(registryName) > 0) {
            return new SimpleResponse("Error deleting registry, registry must be empty", Status.FORBIDDEN);
        }
        
        registryManager.deleteRegistry(registryName);

        return new SimpleResponse(String.format("Registry %s has been deleted", registryName));
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
    public SimpleResponse emptyRegistry(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to delete",
                schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName) throws Exception {

        if(!registryManager.registryExists(registryName)) {
            return new SimpleResponse(String.format(REGISTRY_DOES_NOT_EXIST, registryName),Status.NOT_FOUND);
        }

        registryManager.emptyRegistry(registryName);
        return new SimpleResponse(String.format("Registry %s has been emptied", registryName));
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
    public SimpleResponse getRegistrySchema(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to get schema",
                schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName)
        throws Exception {

        if(!registryManager.registryExists(registryName)) {
            return new SimpleResponse(String.format(REGISTRY_DOES_NOT_EXIST, registryName), Status.NOT_FOUND);
        }

        Optional<String> schemaAsJson = registryManager.getSchemaAsJson(registryName);
        return new SimpleResponse(schemaAsJson.get());
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
    public SimpleResponse updateRegistrySchema(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to update",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName, 
            @RequestBody(description = "Validation schema",
            content = @Content(schema = @Schema(type = STRING))) String validationSchema
    ) throws Exception {

        if(!registryManager.registryExists(registryName)) {
            return new SimpleResponse(String.format(REGISTRY_DOES_NOT_EXIST, registryName),Status.NOT_FOUND);
        }

        registryManager.setSchemaJson(registryName, validationSchema);
        return new SimpleResponse(String.format("/registry/%s/schema", registryName), Status.ACCEPTED);
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
    public SimpleResponse createEntity(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to add to",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName,
            @RequestBody(description = "Entity to create",
            content = @Content(schema = @Schema(type = STRING))) String entity)
                    throws IOException {

        if(!registryManager.registryExists(registryName)) {
            return new SimpleResponse(String.format(REGISTRY_DOES_NOT_EXIST, registryName),Status.NOT_FOUND);
        }

        Optional<String> entityId = entityManager.addEntity(registryName, entity);

        return new SimpleResponse(String.format("/registry/%s/entity/%s", registryName, entityId.get()));
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
    public SimpleResponse entitiesSummary(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to get entity summary from",
                schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName) {

        if(!registryManager.registryExists(registryName)) {
            return new SimpleResponse(String.format(REGISTRY_DOES_NOT_EXIST, registryName),Status.NOT_FOUND);
        }

        return new SimpleResponse("Not implemented", Status.NOT_IMPLEMENTED);
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
    public SimpleResponse getEntity(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to get entity from",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName, 
            @Parameter(in = ParameterIn.PATH, name = ENTITY_ID, required = true,
            description = "Id of entity to get",
                schema = @Schema(type = STRING)) @PathParam(ENTITY_ID) String entityId) {

        if(!registryManager.registryExists(registryName)) {
            return new SimpleResponse(String.format(REGISTRY_DOES_NOT_EXIST, registryName),Status.NOT_FOUND);
        }

        String entityJson = "";

        Optional<String> entry = entityManager.getEntity(registryName, entityId);
        if(entry.isPresent()) {
            entityJson = entry.get();
        }else {
            return new SimpleResponse(String.format(ENTITY_DOES_NOT_EXIST, entityId, registryName), Status.NOT_FOUND);
        }

        return new SimpleResponse(entityJson);
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
    public SimpleResponse deleteEntity(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry to delete entity from",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName, 
            @Parameter(in = ParameterIn.PATH, name = ENTITY_ID, required = true,
            description = "Id of entity to delete",
                schema = @Schema(type = STRING)) @PathParam(ENTITY_ID) String entityId) {
        if(!registryManager.registryExists(registryName)) {
            return new SimpleResponse(String.format(REGISTRY_DOES_NOT_EXIST, registryName),Status.NOT_FOUND);
        }

        if(!entityManager.entityExists(registryName, entityId)) {
            return new SimpleResponse(String.format(ENTITY_DOES_NOT_EXIST, entityId, registryName),Status.NOT_FOUND);
        }

        entityManager.deleteEntity(registryName, entityId);

        return new SimpleResponse(String.format("Entity with id %s is deleted from %s", entityId, registryName));
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
    public SimpleResponse updateEntity(
    		@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
            description = "Name of registry in which to update entity",
            schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName, 
            @Parameter(in = ParameterIn.PATH, name = ENTITY_ID, required = true,
            description = "Id of entity to be updated",
            schema = @Schema(type = STRING)) @PathParam(ENTITY_ID) String entityId, 
            @RequestBody(description = "Entity to update",
                content = @Content(schema = @Schema(type = STRING))) String entity) {

        if(!registryManager.registryExists(registryName)) {
            return new SimpleResponse(String.format(REGISTRY_DOES_NOT_EXIST, registryName),Status.NOT_FOUND);
        }

        if(!entityManager.getEntity(registryName, entityId).isPresent()) {
            return new SimpleResponse(String.format(ENTITY_DOES_NOT_EXIST, entityId, registryName), Status.NOT_FOUND);
        }

        entityManager.updateEntity(registryName, entityId, entity);

        return new SimpleResponse(String.format("Entity with id %s in %s has been updated", entityId, registryName));
    }
}
