package no.bibsys.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.amazonaws.services.s3.Headers;
import com.fasterxml.jackson.core.JsonProcessingException;

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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import no.bibsys.service.EntityService;
import no.bibsys.service.RegistryService;
import no.bibsys.web.model.EntityDto;
import no.bibsys.web.model.RegistryDto;
import no.bibsys.web.security.ApiKeyConstants;
import no.bibsys.web.security.Roles;

@Path("/registry")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@OpenAPIDefinition(info = @Info(title = "Entity Registry", version = "0.0",
        description = "API documentation for Entity Registry",
        license = @License(name = "MIT", url = "https://opensource.org/licenses/MIT"),
        contact = @Contact(url = "http://example.org", name = "Entity registry team",
                email = "entity@example.org")))
@SecurityScheme(name = ApiKeyConstants.API_KEY, paramName = ApiKeyConstants.API_KEY_PARAM_NAME,
        type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
public class DatabaseResource {

    private static final String ENTITY_ID = "entityId";
    private static final String STRING = "string";
    private static final String REGISTRY_NAME = "registryName";
    private final transient RegistryService registryService;
    private final transient EntityService entityService;

    public DatabaseResource(RegistryService registryService, EntityService entityService) {
        this.entityService = entityService;
        this.registryService = registryService;
    }

    @POST
    @Path("/")
    @Operation(extensions = { @Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS,
                    value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
                    value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD, value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
                    value = AwsApiGatewayIntegration.AWS_PROXY), }) })
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({ Roles.API_ADMIN })
    public Response createRegistry(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @RequestBody(description = "Request object to create registry",
                    content = @Content(schema = @Schema(
                            implementation = RegistryDto.class))) RegistryDto registryDto)
            throws JsonProcessingException {

        RegistryDto createdRegistry = registryService.createRegistry(registryDto);
        return Response.ok(createdRegistry).build();
    }

    @GET
    @Path("/")
    @Operation(extensions = { @Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS,
                    value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
                    value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD, value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
                    value = AwsApiGatewayIntegration.AWS_PROXY), }) })
    public Response getRegistryList(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey)
            throws JsonProcessingException {

        List<String> registryList = registryService.getRegistries();
        return Response.ok(registryList).build();
    }


    @GET
    @Path("/{registryName}")
    @Operation(extensions = { @Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS,
                    value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
                    value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD, value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
                    value = AwsApiGatewayIntegration.AWS_PROXY), }) })
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({ Roles.API_ADMIN, Roles.REGISTRY_ADMIN })
    public Response getRegistryMetadata(
            @HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                    description = "Name of new registry",
                    schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName)
            throws IOException {

        RegistryDto registryDto = registryService.getRegistry(registryName);
        return Response.ok(registryDto).build();
    }

    @PUT
    @Path("/{registryName}")
    @Operation(extensions = { @Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS,
                    value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
                    value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD, value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
                    value = AwsApiGatewayIntegration.AWS_PROXY), }) })
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({ Roles.API_ADMIN, Roles.REGISTRY_ADMIN })
    public Response updateRegistryMetadata(
            @HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                    description = "Name of new registry",
                    schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName,
            @RequestBody(description = "Validation schema", content = @Content(schema = @Schema(
                    implementation = RegistryDto.class))) RegistryDto registryDto)
            throws InterruptedException, JsonProcessingException {

        RegistryDto updateRegistry = registryService.updateRegistryMetadata(registryDto);
        return Response.accepted(String.format("Registry %s has been updated", updateRegistry.getId()))
                .build();
    }


    @DELETE
    @Path("/{registryName}")
    @Operation(extensions = { @Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS,
                    value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
                    value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD, value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
                    value = AwsApiGatewayIntegration.AWS_PROXY), }) })
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({ Roles.API_ADMIN, Roles.REGISTRY_ADMIN })
    public Response deleteRegistry(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                    description = "Name of registry to delete",
                    schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName)
            throws InterruptedException {

        registryService.deleteRegistry(registryName);
        return Response.ok(String.format("Registry %s has been deleted", registryName)).build();
    }

    @GET
    @Path("/{registryName}/status")
    @Operation(extensions = { @Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS,
                    value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
                    value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD, value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
                    value = AwsApiGatewayIntegration.AWS_PROXY), }) })
    public Response registryStatus(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                    description = "Name of registry in which to update entity", schema = @Schema(
                            type = STRING)) @PathParam(REGISTRY_NAME) String registryName) {

        registryService.validateRegistryExists(registryName);
        
        return Response.ok(String.format("Registry with name %s is active", registryName)).build();
    }

    @DELETE
    @Path("/{registryName}/empty")
    @Operation(extensions = { @Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS,
                    value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
                    value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD, value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
                    value = AwsApiGatewayIntegration.AWS_PROXY), }) })
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({ Roles.API_ADMIN, Roles.REGISTRY_ADMIN })
    public Response emptyRegistry(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                    description = "Name of registry to delete", schema = @Schema(
                            type = STRING)) @PathParam(REGISTRY_NAME) String registryName) {


        registryService.emptyRegistry(registryName);
        return Response.ok(String.format("Registry %s has been emptied", registryName)).build();
    }

    @GET
    @Path("/{registryName}/schema")
    @Operation(extensions = { @Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS,
                    value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
                    value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD, value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
                    value = AwsApiGatewayIntegration.AWS_PROXY), }) })
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({ Roles.API_ADMIN, Roles.REGISTRY_ADMIN })
    public Response getRegistrySchema(
            @HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                    description = "Name of registry to get schema",
                    schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName)
            throws IOException {

        RegistryDto registryDto = registryService.getRegistry(registryName);
        
        return Response.ok(registryDto).build();
    }

    @PUT
    @Path("/{registryName}/schema")
    @Operation(extensions = { @Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS,
                    value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
                    value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD, value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
                    value = AwsApiGatewayIntegration.AWS_PROXY), }) })
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({ Roles.API_ADMIN, Roles.REGISTRY_ADMIN })
    public Response updateRegistrySchema(
            @HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                    description = "Name of registry to update",
                    schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName,
            @RequestBody(description = "Validation schema",
                    content = @Content(schema = @Schema(type = STRING))) String schema)
            throws IOException {

        RegistryDto updateRegistry = registryService.updateRegistrySchema(registryName, schema);
        updateRegistry.setPath(String.format("/registry/%s/schema", registryName));
        return Response.ok(updateRegistry).build();
    }


    @POST
    @Path("/{registryName}/entity")
    @Operation(extensions = { @Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS,
                    value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
                    value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD, value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
                    value = AwsApiGatewayIntegration.AWS_PROXY), }) })
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({ Roles.API_ADMIN, Roles.REGISTRY_ADMIN })
    public Response createEntity(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                    description = "Name of registry to add to",
                    schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName,
            @RequestBody(description = "Entity to create",
                    content = @Content(schema = @Schema(implementation = EntityDto.class))) EntityDto entityDto)
            throws IOException {

        EntityDto persistedEntity = entityService.addEntity(registryName, entityDto);
        String entityId = persistedEntity.getId();

        persistedEntity.setPath(String.join("/", "registry", registryName, "entity", entityId));

        return Response.ok(persistedEntity).build();
    }

    @GET
    @Path("/{registryName}/entity")
    @Operation(extensions = { @Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS,
                    value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
                    value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD, value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
                    value = AwsApiGatewayIntegration.AWS_PROXY), }) })
    public Response entitiesSummary(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                    description = "Name of registry to get entity summary from", schema = @Schema(
                            type = STRING)) @PathParam(REGISTRY_NAME) String registryName) {

        return Response.status(Status.NOT_IMPLEMENTED).entity("Not implemented").build();
    }

    @POST
    @Path("/{registryName}/upload")
    @Operation(extensions = { @Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS,
                    value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
                    value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD, value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
                    value = AwsApiGatewayIntegration.AWS_PROXY), }) })
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({ Roles.API_ADMIN, Roles.REGISTRY_ADMIN })
    @Produces({MediaType.APPLICATION_JSON})
    public Response uploadEntities(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                    description = "Name of registry to add to",
                    schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName,
            @RequestBody(description = "Array of Entity to upload",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EntityDto.class)))) EntityDto... entityDtos)
            throws IOException {

        
        
        List<EntityDto> persistedEntities = new ArrayList<EntityDto>();
        Arrays.asList(entityDtos).forEach(entityDto -> {
            EntityDto persistedEntity = entityService.addEntity(registryName, entityDto);
            String entityId = persistedEntity.getId();
            persistedEntity.setPath(String.join("/", "registry", registryName, "entity", entityId));
            persistedEntities.add(persistedEntity);
        });

        
        return Response.status(Status.OK).entity(new GenericEntity<List<EntityDto>>(persistedEntities) {}).type(MediaType.APPLICATION_JSON).build();
    }

    
    
    @GET
    @Path("/{registryName}/entity/{entityId}")
    @Operation(extensions = { @Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS,
                    value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
                    value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD, value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
                    value = AwsApiGatewayIntegration.AWS_PROXY), }) })
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    public Response getEntity(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                    description = "Name of registry to get entity from",
                    schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName,
            @Parameter(in = ParameterIn.PATH, name = ENTITY_ID, required = true,
                    description = "Id of entity to get",
                    schema = @Schema(type = STRING)) @PathParam(ENTITY_ID) String entityId, @Context Request request) throws JsonProcessingException {

        
        EntityDto entity = entityService.getEntity(registryName, entityId);
        
        EntityTag etag = new EntityTag(entity.getEtagValue());
        
        ResponseBuilder builder = request.evaluatePreconditions(etag);
        if (builder != null) {
            return builder.build();
        }
                
        return Response.ok(entity).tag(etag).header(Headers.LAST_MODIFIED, entity.getModified()).build();
    }

    @DELETE
    @Path("/{registryName}/entity/{entityId}")
    @Operation(extensions = { @Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS,
                    value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
                    value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD, value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
                    value = AwsApiGatewayIntegration.AWS_PROXY), }) })
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({ Roles.API_ADMIN })
    public Response deleteEntity(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                    description = "Name of registry to delete entity from",
                    schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName,
            @Parameter(in = ParameterIn.PATH, name = ENTITY_ID, required = true,
                    description = "Id of entity to delete",
                    schema = @Schema(type = STRING)) @PathParam(ENTITY_ID) String entityId) {

        entityService.deleteEntity(registryName, entityId);

        return Response
                .ok(String.format("Entity with id %s is deleted from %s", entityId, registryName))
                .build();
    }

    @PUT
    @Path("/{registryName}/entity/{entityId}")
    @Operation(extensions = { @Extension(name = AwsApiGatewayIntegration.INTEGRATION, properties = {
            @ExtensionProperty(name = AwsApiGatewayIntegration.URI,
                    value = AwsApiGatewayIntegration.URI_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.REQUEST_PARAMETERS,
                    value = AwsApiGatewayIntegration.REQUEST_PARAMETERS_OBJECT, parseValue = true),
            @ExtensionProperty(name = AwsApiGatewayIntegration.PASSTHROUGH_BEHAVIOR,
                    value = AwsApiGatewayIntegration.WHEN_NO_MATCH),
            @ExtensionProperty(name = AwsApiGatewayIntegration.HTTPMETHOD, value = HttpMethod.POST),
            @ExtensionProperty(name = AwsApiGatewayIntegration.TYPE,
                    value = AwsApiGatewayIntegration.AWS_PROXY), }) })
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({ Roles.API_ADMIN, Roles.REGISTRY_ADMIN })
    public Response updateEntity(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
            @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                    description = "Name of registry in which to update entity",
                    schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName,
            @Parameter(in = ParameterIn.PATH, name = ENTITY_ID, required = true,
                    description = "Id of entity to be updated",
                    schema = @Schema(type = STRING)) @PathParam(ENTITY_ID) String entityId,
            @RequestBody(description = "Entity to update",
                    content = @Content(schema = @Schema(implementation = EntityDto.class))) EntityDto entityDto) {

        entityService.updateEntity(registryName, entityDto);

        return Response.ok(
                String.format("Entity with id %s in %s has been updated", entityId, registryName))
                .build();
    }
}
