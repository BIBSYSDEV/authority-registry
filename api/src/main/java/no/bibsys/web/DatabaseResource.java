package no.bibsys.web;

import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION;
import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI;
import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE;
import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD;
import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR;
import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE;
import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTP_PROXY;
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
import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import no.bibsys.db.DatabaseManager;
import no.bibsys.web.model.CreateRegistryRequest;
import no.bibsys.web.model.EditRegistryRequest;
import no.bibsys.web.model.EmptyRegistryRequest;
import no.bibsys.web.model.PathResponse;
import no.bibsys.web.model.SimpleResponse;

@Path("/registry")
public class DatabaseResource {

    private transient final static String CONTENT_TYPE = "application/json;charset=UTF-8";
    
    private transient final DatabaseManager databaseManager;

    public DatabaseResource(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    //Fn::Sub: 
    
    @POST
    @Path("/")
    @Consumes(CONTENT_TYPE)
    @Produces(CONTENT_TYPE)
    @Operation(
            extensions = {
                    @Extension(
                            name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION,
                            properties = {
                                    @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI, value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE),
                                    @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR, value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH),
                                    @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD, value = HttpMethod.POST),
                                    @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE, value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTP_PROXY),
                            }
                    )
            }
    )
    public SimpleResponse editRegistry(EditRegistryRequest request)
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
    @Consumes(CONTENT_TYPE)
    @Produces(CONTENT_TYPE)
    @Operation(
            extensions = {
                    @Extension(
                            name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION,
                            properties = {
                                    @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI, value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE),
                                    @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR, value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH),
                                    @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD, value = HttpMethod.PUT),
                                    @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE, value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTP_PROXY),
                            }
                    )
            }
    )
    public SimpleResponse putNewRegistry(@PathParam("registryName") String registryName,
            String validationSchema) throws InterruptedException, JsonProcessingException {
        return createTable(new CreateRegistryRequest(registryName, validationSchema));

    }


    @POST
    @Path("/{registryName}")
    @Consumes(CONTENT_TYPE)
    @Produces(CONTENT_TYPE)
    @Operation(
            extensions = {
                    @Extension(
                            name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION,
                            properties = {
                                    @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI, value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE),
                                    @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR, value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH),
                                    @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD, value = HttpMethod.POST),
                                    @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE, value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTP_PROXY),
                            }
                    )
            }
    )
    public PathResponse insertEntry(@PathParam("registryName") String registryName, String request)
            throws IOException {
        databaseManager.insertEntry(registryName, request);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(request);
        String id = node.get("id").asText();
        return new PathResponse(String.format("/registry/%s/%s", registryName, id));
    }


    @DELETE
    @Path("/{registryName}")
    @Consumes(CONTENT_TYPE)
    @Produces(CONTENT_TYPE)
    @Operation(
            extensions = {
                    @Extension(
                            name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION,
                            properties = {
                                    @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI, value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE),
                                    @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR, value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH),
                                    @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD, value = HttpMethod.DELETE),
                                    @ExtensionProperty(name = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE, value = AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTP_PROXY),
                            }
                    )
            }
    )
    public SimpleResponse deleteRegistry(@PathParam("registryName") String registryName)
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
