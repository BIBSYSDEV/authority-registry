package no.bibsys.web;

public class AwsExtensionHelper {

  public static final String AWS_X_AMAZON_APIGATEWAY_INTEGRATION = "x-amazon-apigateway-integration";
  public static final String AWS_X_AMAZON_APIGATEWAY_INTEGRATION_REQUEST_PARAMETERS = "requestParameters";
  public static final String AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE = "type";
  public static final String AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD = "httpMethod";
  public static final String AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI = "uri";
  public static final String AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI_VALUE = "{\"Fn::Sub\": \"arn:aws:apigateway:${AWS::Region}:lambda:path/2015-03-31/functions/${LambdaFunction.Arn}/invocations\"}";
  public static final String AWS_X_AMAZON_APIGATEWAY_INTEGRATION_REQUEST_RESPONSES = "responses";
  public static final String AWS_X_AMAZON_APIGATEWAY_INTEGRATION_PASSTHROUGH_BEHAVIOR = "passthroughBehavior";
  public static final String AWS_X_AMAZON_APIGATEWAY_INTEGRATION_AWS_PROXY = "aws_proxy";
  public static final String AWS_X_AMAZON_APIGATEWAY_INTEGRATION_WHEN_NO_MATCH = "when_no_match";
  
  
  public static final String AWS_REQUEST_PARAMETERS_OBJECT = "{\"integration.request.header.x-api-key\": \"method.request.header.x-api-key\"}";
}
