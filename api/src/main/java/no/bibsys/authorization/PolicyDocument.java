package no.bibsys.authorization;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PolicyDocument represents an IAM Policy, specifically for the execute-api:Invoke action in the
 * context of a API Gateway Authorizer
 *
 * Initialize the PolicyDocument with the region where the RestApi is configured, the AWS Account ID
 * that owns the RestApi, the RestApi identifier and the Stage on the RestApi that the Policy will
 * apply to
 */
public class PolicyDocument {

    //static final String EXECUTE_API_ARN_FORMAT = "arn:aws:execute-api:%s:%s:%s/%s/%s/%s";


    @JsonProperty("Version")
    private String version = "2012-10-17"; // override if necessary



    @JsonProperty
    private String principalId;

    @JsonProperty("Statement")
    private List<Statement> statements;

    @JsonIgnore
    private transient String region;
    @JsonIgnore
    private transient String awsAccountId;
    @JsonIgnore
    private transient String restApiId;
    @JsonIgnore
    private transient String stage;

    // context metadata


    /**
     * Creates a new PolicyDocument with the given context, and initializes two base Statement
     * objects for allowing and denying access to API Gateway methods
     *
     * @param region the region where the RestApi is configured
     * @param awsAccountId the AWS Account ID that owns the RestApi
     * @param restApiId the RestApi identifier
     * @param stage and the Stage on the RestApi that the Policy will apply to
     */
    public PolicyDocument(String principalId, String region, String awsAccountId, String restApiId,
        String stage) {
        this.principalId = principalId;
        this.region = region;
        this.awsAccountId = awsAccountId;
        this.restApiId = restApiId;
        this.stage = stage;
        this.statements = new ArrayList<>();

    }

    /**
     * Generates a new PolicyDocument with a single statement that allows the requested
     * method/resourcePath
     *
     * @param region API Gateway region
     * @param awsAccountId AWS Account that owns the API Gateway RestApi
     * @param restApiId RestApi identifier
     * @param stage Stage name
     * @param method HttpMethod to allow
     * @param resourcePath Resource path to allow
     * @return new PolicyDocument that allows the requested method/resourcePath
     */
    public static PolicyDocument getAllowOnePolicy(String principalId, String region,
        String awsAccountId,
        String restApiId, String stage, HttpMethod method, String resourcePath) {
        PolicyDocument policyDocument = new PolicyDocument(principalId, region,
            awsAccountId, restApiId, stage);
        Resource resource = new Resource(region, awsAccountId, restApiId, stage, method,
            resourcePath);

        Statement statement = new Statement(Statement.ALLOW_EFFECT,
            Collections.singletonList(Statement.ACTION_API_INVOKE),
            resource,
            Collections.emptyMap()
        );

        policyDocument.addStatement(statement);

        return policyDocument;

    }

    /**
     * Generates a new PolicyDocument with a single statement that denies the requested
     * method/resourcePath
     *
     * @param region API Gateway region
     * @param awsAccountId AWS Account that owns the API Gateway RestApi
     * @param restApiId RestApi identifier
     * @param stage Stage name
     * @param method HttpMethod to deny
     * @param resourcePath Resource path to deny
     * @return new PolicyDocument that denies the requested method/resourcePath
     */
    public static PolicyDocument getDenyOnePolicy(String region, String awsAccountId,
        String restApiId, String stage, HttpMethod method, String resourcePath) {
        PolicyDocument policyDocument = new PolicyDocument("lalala", region,
            awsAccountId, restApiId, stage);

        return policyDocument;

    }

    public static PolicyDocument getAllowAllPolicy(String principalId, String region,
        String awsAccountId, String restApiId, String stage) {

        return getAllowOnePolicy(principalId,
            region,
            awsAccountId,
            restApiId,
            stage,
            HttpMethod.ALL,
            Resource.ANY_RESOURCE.toString());
    }

    public static PolicyDocument getDenyAllPolicy(String region, String awsAccountId,
        String restApiId, String stage) {
        return getDenyOnePolicy(region, awsAccountId, restApiId, stage, HttpMethod.ALL,
            Resource.ANY_RESOURCE.toString());
    }


    public void addStatement(Statement statement) {
        statements.add(statement);
    }




}