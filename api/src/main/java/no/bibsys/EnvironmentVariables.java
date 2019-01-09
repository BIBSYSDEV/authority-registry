package no.bibsys;

public class EnvironmentVariables {

    /** Deployment stage ("test or final").*/
    public static final String STAGE_NAME = "STAGE_NAME";

    public static final String API_KEY_TABLE_NAME = "API_KEY_TABLE_NAME";
    public static final String REGISTRY_METADATA_TABLE_NAME = "VALIDATION_SCHEMA_TABLE_NAME";

    /** Stack name. Should be always equal to !Ref AWS::Stack_name.*/
    public static final String STACK_NAME = "STACK_NAME";

    /** Arn of a regional certificate stored in Certificate manager in AWS.*/
    public static final String CERTIFICATE_ARN_ENV = "REGIONAL_CERTIFICATE_ARN";

    /** Name of the used Route53 Hosted Zone. */
    public static final String HOSTED_ZONE_NAME = "HOSTED_ZONE_NAME";

    /** Static URL of the application that will make the application accessible to the world. */
    public static final String APPLICATION_URL = "APPLICATION_URL";


    /** Git branch */
    public static final String BRANCH="BRANCH";


}
