package no.bibsys.web;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import no.bibsys.web.security.ApiKeyConstants;

@Path("/ping")
public class PingResource {

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
    public Response ping(@HeaderParam(ApiKeyConstants.API_KEY_PARAM_NAME) String apiKey) {
        return Response.ok().build();
    }
}
