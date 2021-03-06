package no.bibsys;

public class EnvironmentVariables {

    /**
     * Deployment stage ("test or final").
     */
    public static final String STAGE_NAME = "STAGE_NAME";

    public static final String API_KEY_TABLE_NAME = "API_KEY_TABLE_NAME";
    public static final String REGISTRY_METADATA_TABLE_NAME = "VALIDATION_SCHEMA_TABLE_NAME";

    /**
     * Stack name. Should be always equal to !Ref AWS::Stack_name.
     */
    public static final String STACK_NAME = "STACK_NAME";

    /**
     * Arn of a regional certificate stored in Certificate manager in AWS.
     */
    public static final String CERTIFICATE_ARN_ENV = "REGIONAL_CERTIFICATE_ARN";

    /**
     * Name of the used Route53 Hosted Zone.
     */
    public static final String HOSTED_ZONE_NAME = "HOSTED_ZONE_NAME";

    /**
     * Static URL of the application that will make the application accessible to the world.
     */
    public static final String APPLICATION_URL = "APPLICATION_URL";

    /**
     * Static URL of the cloudSearch domain that will give us the endpoints for searching and document upload data.
     */
    public static final String CLOUDSEARCH_DOMAIN = "CLOUDSEARCH_DOMAIN";
    public static final String CLOUDSEARCH_SEARCH_ENDPOINT = "CLOUDSEARCH_SEARCH_ENDPOINT";

    

    /**
     * Git branch.
     */
    public static final String BRANCH = "BRANCH";

    public static final String SWAGGER_API_ID = "SWAGGER_API_ID";

    public static final String SWAGGER_API_VERSION = "SWAGGER_API_VERSION";

    public static final String SWAGGER_API_OWNER = "SWAGGER_API_OWNER";

    public static final String SWAGGERHUB_API_KEY_SECRET_NAME = "SWAGGERHUB_API_KEY_SECRET_NAME";
    public static final String SWAGGERHUB_API_KEY_SECRET_KEY = "SWAGGERHUB_API_KEY_SECRET_KEY";
}
