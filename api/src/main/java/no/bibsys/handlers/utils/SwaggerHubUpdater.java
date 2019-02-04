package no.bibsys.handlers.utils;

import com.amazonaws.services.apigateway.AmazonApiGateway;
import com.amazonaws.services.apigateway.AmazonApiGatewayClientBuilder;
import com.amazonaws.services.apigateway.model.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import no.bibsys.aws.apigateway.ApiGatewayApiInfo;
import no.bibsys.aws.apigateway.ServerInfo;
import no.bibsys.aws.cloudformation.Stage;
import no.bibsys.aws.cloudformation.helpers.ResourceType;
import no.bibsys.aws.cloudformation.helpers.StackResources;
import no.bibsys.aws.swaggerhub.SwaggerDriver;
import no.bibsys.aws.swaggerhub.SwaggerHubInfo;
import no.bibsys.handlers.GitConstants;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwaggerHubUpdater {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerHubUpdater.class);

    private static final String SERVERS_FIELD = "servers";
    private static final String URL_FIELD = "url";

    private static final String STACK_NOT_FOUND_MESSAGE = "RestApi not Found for stack: ";
    private static final String FAILURE_WITH_SWAGGERHUB = "Could not generate SwaggerHub specification";

    private final transient ObjectMapper jsonParser = Json.mapper(); //Swagger specific ObjectMapper
    private final transient String apiId;
    private final transient String apiVersion;
    private final transient String swaggerHubOrganization;
    private final transient String stackName;
    private final transient Stage stage;
    private final transient String branchName;

    public SwaggerHubUpdater(String apiId, String apiVersion, String swaggerHubOrganization, String stackName,
        Stage stage, String branchName) {
        this.apiId = apiId;
        this.apiVersion = apiVersion;
        this.swaggerHubOrganization = swaggerHubOrganization;
        this.stackName = stackName;
        this.stage = stage;
        this.branchName= branchName;
    }

    public void updateSwaggerHub() throws IOException, URISyntaxException {

        try {
            ObjectNode swaggerRoot = (ObjectNode) jsonParser.readTree(generateOpenApiSpecificationFromCode());
            Optional<ObjectNode> updatedSwaggerRootDoc = updateSwaggerRootWithServerInfoFromApiGateway(swaggerRoot);

            if (updatedSwaggerRootDoc.isPresent()) {
                SwaggerHubInfo swaggerHubInfo = initializeSwaggerHubInfo();
                SwaggerDriver swaggerDriver = new SwaggerDriver(swaggerHubInfo);
                String apiKey = swaggerHubInfo.getSwaggerAuth();
                deletePreviousSwaggerHubSpecification(swaggerDriver, apiKey);
                updateSwaggerHubSpecification(updatedSwaggerRootDoc.get(), swaggerHubInfo, swaggerDriver);
            } else {
                logger.error(FAILURE_WITH_SWAGGERHUB);
            }
        } catch (OpenApiConfigurationException e) {
            logger.error(e.getMessage());
            throw new IOException(e);
        }
    }

    private SwaggerHubInfo initializeSwaggerHubInfo() {
        if(branchName.equals(GitConstants.MASTER_BRANCH)){
            return new SwaggerHubInfo(apiId, apiVersion, swaggerHubOrganization);
        }
        else{
            return new SwaggerHubInfo(stackName,apiVersion,swaggerHubOrganization);
        }

    }

    private void updateSwaggerHubSpecification(ObjectNode updatedSwaggerRootDoc, SwaggerHubInfo swaggerHubInfo,
        SwaggerDriver swaggerDriver) throws URISyntaxException, IOException {
        String swaggerString = Json.pretty(updatedSwaggerRootDoc);
        HttpPost updateRequest = swaggerDriver.createUpdateRequest(swaggerString, swaggerHubInfo.getSwaggerAuth());
        swaggerDriver.executePost(updateRequest);
    }

    private void deletePreviousSwaggerHubSpecification(SwaggerDriver swaggerDriver, String apiKey)
        throws URISyntaxException, IOException {
        HttpDelete deleteRequest = swaggerDriver.createDeleteApiRequest(apiKey);
        swaggerDriver.executeDelete(deleteRequest);
    }

    private String generateOpenApiSpecificationFromCode() throws OpenApiConfigurationException {
        OpenApiContext context = new JaxrsOpenApiContextBuilder().buildContext(true);
        return Json.pretty(context.read());
    }

    private Optional<ObjectNode> updateSwaggerRootWithServerInfoFromApiGateway(ObjectNode swaggerDocRoot)
        throws IOException {
        Optional<ServerInfo> serverInfo = readServerInfo();
        Optional<ObjectNode> newDoc = serverInfo.map(si -> updateSwaggerHubDocWithServerInfo(swaggerDocRoot, si));
        return newDoc;
    }

    private ObjectNode updateSwaggerHubDocWithServerInfo(ObjectNode openApiDocRoot, ServerInfo serverInfo) {
        ArrayNode serversNode = serversNode(serverInfo);
        return (ObjectNode) openApiDocRoot.set(SERVERS_FIELD, serversNode);
    }

    private ArrayNode serversNode(ServerInfo serverInfo) {
        ArrayNode servers = jsonParser.createArrayNode();
        ObjectNode serverNode = jsonParser.createObjectNode();
        serverNode.put(URL_FIELD, serverInfo.getServerUrl());
        servers.add(serverNode);
        return servers;
    }

    private Optional<ServerInfo> readServerInfo() throws IOException {
        String restApiId = restApiId(stackName);
        AmazonApiGateway apiGatewayClient = AmazonApiGatewayClientBuilder.defaultClient();
        ApiGatewayApiInfo apiGatewayApiInfo = new ApiGatewayApiInfo(stage, apiGatewayClient, restApiId);
        return apiGatewayApiInfo.readServerInfo();
    }

    private String restApiId(String stackName) {
        StackResources stackResources = new StackResources(stackName);
        return stackResources.getResourceIds(ResourceType.REST_API).stream().findAny()
            .orElseThrow(() -> new NotFoundException(STACK_NOT_FOUND_MESSAGE + stackName));
    }
}
