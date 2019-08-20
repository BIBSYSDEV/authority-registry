package no.bibsys.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.amazonaws.services.s3.Headers;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import no.bibsys.entitydata.validation.exceptions.EntityFailedShaclValidationException;
import no.bibsys.entitydata.validation.exceptions.ShaclModelValidationException;
import no.bibsys.entitydata.validation.exceptions.TargetClassPropertyObjectIsNotAResourceException;
import no.bibsys.service.EntityService;
import no.bibsys.service.RegistryService;
import no.bibsys.service.SearchService;
import no.bibsys.service.exceptions.UnknownStatusException;
import no.bibsys.service.exceptions.ValidationSchemaNotFoundException;
import no.bibsys.web.model.CustomMediaType;
import no.bibsys.web.model.EntityDto;
import no.bibsys.web.model.RegistryCreateRequestParametersObject;
import no.bibsys.web.model.RegistryDto;
import no.bibsys.web.model.RegistryInfoNoMetadataDto;
import no.bibsys.web.security.ApiKeyConstants;
import no.bibsys.web.security.Roles;

@Path("/registry")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})

@SecurityScheme(name = ApiKeyConstants.API_KEY_PARAM_NAME, type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER)
public class DatabaseResource {

    private static final String NAME_OF_REGISTRY_TO = "Name of registry to ";
    private static final String NAME_OF_REGISTRY_IN = "Name of registry in ";
    private static final String NAME_OF_NEW_REGISTRY = "Name of new registry";
    private static final String ENTITY_ID = "entityId";
    private static final String STRING = "string";
    private static final String REGISTRY_NAME = "registryName";
    public static final String PATH_DELIMITER = "/";
    private final transient RegistryService registryService;
    private final transient EntityService entityService;
    private final transient SearchService searchService;

    public DatabaseResource(
            RegistryService registryService, 
            EntityService entityService, 
            SearchService searchService) {
        this.entityService = entityService;
        this.registryService = registryService;
        this.searchService = searchService;
    }

    @POST
    @Path("/")
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    public Response createRegistry(@RequestBody(description = "Request object to create registry",
            content = @Content(schema = @Schema(implementation = RegistryCreateRequestParametersObject.class)))
                                           RegistryDto registryDto) throws Exception {

        RegistryInfoNoMetadataDto createdRegistry = registryService.createRegistry(registryDto);
        return Response.ok(createdRegistry).build();
    }

    @GET
    @Path("/")
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    public Response getRegistryList(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey) {

        List<String> registryList = registryService.getRegistries();
        return Response.ok(registryList).build();
    }

    @GET
    @Path("/{registryName}")
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON, CustomMediaType.APPLICATION_RDF,
            CustomMediaType.APPLICATION_JSON_LD, CustomMediaType.APPLICATION_N_TRIPLES,
            CustomMediaType.APPLICATION_RDF_XML, CustomMediaType.APPLICATION_TURTLE})
    public Response getRegistryMetadata(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
                                        @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                                                description = NAME_OF_NEW_REGISTRY, schema = @Schema(type = STRING))
                                        @PathParam(REGISTRY_NAME) String registryName) {

        RegistryDto registryDto = registryService.getRegistry(registryName);
        return Response.ok(registryDto).build();
    }

    @PUT
    @Path("/{registryName}")
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN, Roles.REGISTRY_ADMIN})
    public Response updateRegistryMetadata(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
                                           @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                                                   description = NAME_OF_NEW_REGISTRY, schema = @Schema(type = STRING))
                                           @PathParam(REGISTRY_NAME) String registryName,
                                           @RequestBody(description = "Validation schema", content = @Content(
                                                   schema = @Schema(implementation = RegistryDto.class)))
                                                   RegistryDto registryDto) throws IOException {

        RegistryDto updateRegistry = registryService.updateRegistryMetadata(registryDto);
        return Response.accepted(String.format("Registry %s has been updated", updateRegistry.getId())).build();
    }

    @DELETE
    @Path("/{registryName}")
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN, Roles.REGISTRY_ADMIN})
    public Response deleteRegistry(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
                                   @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                                           description = NAME_OF_REGISTRY_TO + "delete",
                                           schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME)
                                           String registryName) {

        registryService.deleteRegistry(registryName);
        return Response.ok(String.format("Registry %s has been deleted", registryName)).build();
    }

    @GET
    @Path("/{registryName}/status")

    public Response registryStatus(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
                                   @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                                           description = NAME_OF_REGISTRY_IN + "which to get status",
                                           schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME)
                                           String registryName) throws UnknownStatusException {

        registryService.validateRegistryExists(registryName);
        return Response.ok(String.format("Registry with name %s is active", registryName)).build();
    }

    @PUT
    @Path("/{registryName}/apikey")
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response replaceApiKey(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
                                  @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                                          description = NAME_OF_REGISTRY_IN + "which to update entity",
                                          schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME)
                                          String registryName, @RequestBody(description = "Old apikey",
            content = @Content(schema = @Schema(implementation = String.class))) String oldApiKey)
            throws UnknownStatusException {

        registryService.validateRegistryExists(registryName);
        String newApiKey = registryService.replaceApiKey(registryName, oldApiKey);
        return Response.ok(newApiKey).build();
    }

    @GET
    @Path("/{registryName}/schema")
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN, Roles.REGISTRY_ADMIN})
    public Response getRegistrySchema(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
                                      @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                                              description = NAME_OF_REGISTRY_TO + "get schema",
                                              schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME)
                                              String registryName) throws JsonProcessingException {

        RegistryInfoNoMetadataDto registryDto = registryService.getRegistryInfo(registryName);
        return Response.ok(registryDto).build();
    }

    @GET
    @Path("/{registryName}/search")
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN, Roles.REGISTRY_ADMIN})
    public Response queryRegistry(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
                                  @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                                              description = NAME_OF_REGISTRY_TO + "query",
                                              schema = @Schema(type = STRING)) 
                                  @PathParam(REGISTRY_NAME) String registryName,
                                  @QueryParam("query") String queryString

                                  ) throws JsonProcessingException {
        
        List<String> queryResult = searchService.simpleQuery(registryName, queryString);
        return Response.ok().entity(queryResult).build();
    }
    
    
    
    @PUT
    @Path("/{registryName}/schema")
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN, Roles.REGISTRY_ADMIN})
    public Response updateRegistrySchema(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
                                         @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                                                 description = NAME_OF_REGISTRY_TO + "update",
                                                 schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME)
                                                 String registryName, @RequestBody(description = "Validation schema",
            content = @Content(schema = @Schema(type = STRING))) String schema)

            throws IOException, ShaclModelValidationException, TargetClassPropertyObjectIsNotAResourceException {

        RegistryDto updateRegistry = registryService.updateRegistrySchema(registryName, schema);
        updateRegistry.setPath(String.format("/registry/%s/schema", registryName));
        return Response.ok(updateRegistry).build();
    }

    @POST
    @Path("/{registryName}/entity")
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN, Roles.REGISTRY_ADMIN})
    public Response createEntity(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
                                 @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                                         description = NAME_OF_REGISTRY_TO + "add to", schema = @Schema(type = STRING))
                                 @PathParam(REGISTRY_NAME) String registryName,
                                 @RequestBody(description = "Entity to create",
                                         content = @Content(schema = @Schema(implementation = EntityDto.class)))
                                         EntityDto entityDto,
                                 @Context UriInfo uriInfo)
            throws EntityFailedShaclValidationException, ValidationSchemaNotFoundException, JsonProcessingException {

        EntityDto persistedEntity = entityService.addEntity(registryName, entityDto);
        String entityId = persistedEntity.getId();
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        String entityPath = String.join(PATH_DELIMITER, entityId);
        uriBuilder.path(entityPath);
        persistedEntity.setPath(entityPath);
        return Response.created(uriBuilder.build()).entity(persistedEntity).build();
    }

    @POST
    @Path("/{registryName}/upload")
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN, Roles.REGISTRY_ADMIN})
    @Produces({MediaType.APPLICATION_JSON})
    public Response uploadEntities(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
                                   @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                                           description = NAME_OF_REGISTRY_TO + "add to",
                                           schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME)
                                           String registryName, @RequestBody(description = "Array of Entity to upload",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = EntityDto.class))))
                                           EntityDto... entityDtos)
            throws EntityFailedShaclValidationException, ValidationSchemaNotFoundException, JsonProcessingException {

        List<EntityDto> persistedEntities = new ArrayList<>();
        for (EntityDto entityDto : entityDtos) {
            EntityDto persistedEntity = entityService.addEntity(registryName, entityDto);
            String entityId = persistedEntity.getId();
            persistedEntity.setPath(String.join("/", "registry", registryName, "entity", entityId));
            persistedEntities.add(persistedEntity);
        }

        return Response.status(Status.OK).entity(new GenericEntity<List<EntityDto>>(persistedEntities) {}).type(
                MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/{registryName}/entity/{entityId}")
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)

    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON, CustomMediaType.APPLICATION_RDF,
            CustomMediaType.APPLICATION_RDF_XML, CustomMediaType.APPLICATION_JSON_LD,
            CustomMediaType.APPLICATION_N_TRIPLES, CustomMediaType.APPLICATION_TURTLE, CustomMediaType.APPLICATION_MARC,
            CustomMediaType.APPLICATION_MARCXML, CustomMediaType.APPLICATION_MARCXML_XML})
    public Response getEntity(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
                              @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                                      description = NAME_OF_REGISTRY_TO + "get entity " + "from",
                                      schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName,
                              @Parameter(in = ParameterIn.PATH, name = ENTITY_ID, required = true,
                                      description = "Id of" + " entity to get", schema = @Schema(type = STRING))
                              @PathParam(ENTITY_ID) String entityId, @Context Request request)
            throws JsonProcessingException {

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
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN, Roles.REGISTRY_ADMIN})
    public Response deleteEntity(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
                                 @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                                         description = NAME_OF_REGISTRY_TO + "delete entity " + "from",
                                         schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName,
                                 @Parameter(in = ParameterIn.PATH, name = ENTITY_ID, required = true,
                                         description = "Id of" + " entity to delete", schema = @Schema(type = STRING))
                                 @PathParam(ENTITY_ID) String entityId) {

        entityService.deleteEntity(registryName, entityId);

        return Response.ok(String.format("Entity with id %s is deleted from %s", entityId, registryName)).build();
    }

    @PUT
    @Path("/{registryName}/entity/{entityId}")
    @SecurityRequirement(name = ApiKeyConstants.API_KEY)
    @RolesAllowed({Roles.API_ADMIN, Roles.REGISTRY_ADMIN})
    public Response updateEntity(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey,
                                 @Parameter(in = ParameterIn.PATH, name = REGISTRY_NAME, required = true,
                                         description = NAME_OF_REGISTRY_TO + "which to update" + " entity",
                                         schema = @Schema(type = STRING)) @PathParam(REGISTRY_NAME) String registryName,
                                 @Parameter(in = ParameterIn.PATH, name = ENTITY_ID, required = true,
                                         description = "Id of" + " entity to be " + "updated",
                                         schema = @Schema(type = STRING)) @PathParam(ENTITY_ID) String entityId,
                                 @RequestBody(description = "Entity to update",
                                         content = @Content(schema = @Schema(implementation = EntityDto.class)))
                                         EntityDto entityDto)
            throws ValidationSchemaNotFoundException, EntityFailedShaclValidationException, JsonProcessingException {

        EntityDto entity = entityService.updateEntity(registryName, entityDto);

        return Response.ok().entity(entity).build();
    }
}
