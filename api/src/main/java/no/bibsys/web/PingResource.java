package no.bibsys.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import no.bibsys.web.security.ApiKeyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/ping")
public class PingResource {

    private static final Logger logger = LoggerFactory.getLogger(PingResource.class);

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
        if (apiKey == null) {
            System.out.println("APIKEY is null");
            logger.info("APIKEY is null");
        }
        System.out.println(apiKey);
        logger.info("APIKEY:" + apiKey);
        return Response.ok().build();
    }
}
