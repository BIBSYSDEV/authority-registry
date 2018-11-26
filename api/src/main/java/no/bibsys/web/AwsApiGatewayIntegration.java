package no.bibsys.web;

public class AwsApiGatewayIntegration {

    public static final String INTEGRATION = "x-amazon-apigateway-integration";
    public static final String REQUEST_PARAMETERS = "requestParameters";
    public static final String TYPE = "type";
    public static final String HTTPMETHOD = "httpMethod";
    public static final String URI = "uri";
    public static final String URI_OBJECT =
            "{\"Fn::Sub\": \"arn:aws:apigateway:${AWS::Region}:lambda:path/2015-03-31/functions/${LambdaFunction.Arn}/invocations\"}";
    public static final String RESPONSES = "responses";
    public static final String PASSTHROUGH_BEHAVIOR = "passthroughBehavior";
    public static final String AWS_PROXY = "aws_proxy";
    public static final String WHEN_NO_MATCH = "when_no_match";


    public static final String REQUEST_PARAMETERS_OBJECT =
            "{\"integration.request.header.api-key\": \"method.request.header.api-key\"}";
}
