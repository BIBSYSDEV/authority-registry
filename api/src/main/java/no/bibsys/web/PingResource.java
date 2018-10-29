package no.bibsys.web;

import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION;
import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION_AWS_PROXY;
import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD;
import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR;
import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE;
import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI;
import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE;
import static no.bibsys.web.AwsExtensionHelper.AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;

@Path("/ping")
public class PingResource {

    @HEAD
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
    public Response pingHead() {
        return Response.ok().build();
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
    public Response pingGet() {
        return Response.ok().build();
    }
}
