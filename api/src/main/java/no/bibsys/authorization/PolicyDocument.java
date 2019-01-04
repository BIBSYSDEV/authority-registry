package no.bibsys.authorization;

import java.util.ArrayList;
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


    //arn:aws:execute-api:{regionId}:{accountId}:{appId}/{stage}/{httpVerb}/[{resource}/[child-resources]]
    static final String EXECUTE_API_ARN_FORMAT = "arn:aws:execute-api:%s:%s:%s/%s/%s/%s";

    String Version = "2012-10-17"; // override if necessary
    // context metadata
    private transient String region;
    private transient String awsAccountId;
    private transient String restApiId;
    private transient String stage;
    private Statement allowStatement;
    private Statement denyStatement;
    private List<Statement> statements;

    /**
     * Creates a new PolicyDocument with the given context, and initializes two base Statement
     * objects for allowing and denying access to API Gateway methods
     *
     * @param region the region where the RestApi is configured
     * @param awsAccountId the AWS Account ID that owns the RestApi
     * @param restApiId the RestApi identifier
     * @param stage and the Stage on the RestApi that the Policy will apply to
     */
    public PolicyDocument(String region, String awsAccountId, String restApiId, String stage) {
        this.region = region;
        this.awsAccountId = awsAccountId;
        this.restApiId = restApiId;
        this.stage = stage;
        allowStatement = Statement.getEmptyInvokeStatement("Allow");
        denyStatement = Statement.getEmptyInvokeStatement("Deny");
        this.statements = new ArrayList<>();
        statements.add(allowStatement);
        statements.add(denyStatement);
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
    public static PolicyDocument getAllowOnePolicy(String region, String awsAccountId,
        String restApiId, String stage, HttpMethod method, String resourcePath) {
        PolicyDocument policyDocument = new PolicyDocument(region,
            awsAccountId, restApiId, stage);
        policyDocument.allowMethod(method, resourcePath);
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
        PolicyDocument policyDocument = new PolicyDocument(region,
            awsAccountId, restApiId, stage);
        policyDocument.denyMethod(method, resourcePath);
        return policyDocument;

    }

    public static PolicyDocument getAllowAllPolicy(String region,
        String awsAccountId, String restApiId, String stage) {
        return getAllowOnePolicy(region, awsAccountId, restApiId, stage, HttpMethod.ALL, "*");
    }

    public static PolicyDocument getDenyAllPolicy(String region, String awsAccountId,
        String restApiId, String stage) {
        return getDenyOnePolicy(region, awsAccountId, restApiId, stage, HttpMethod.ALL, "*");
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public Statement[] getStatement() {
        return statements.toArray(new Statement[statements.size()]);
    }

    // Static methods

    public void allowMethod(HttpMethod httpMethod, String resourcePath) {
        addResourceToStatement(allowStatement, httpMethod, resourcePath);
    }

    public void denyMethod(HttpMethod httpMethod, String resourcePath) {
        addResourceToStatement(denyStatement, httpMethod, resourcePath);
    }

    public void addStatement(Statement statement) {
        statements.add(statement);
    }

    private void addResourceToStatement(Statement statement, HttpMethod httpMethod,
        String resourcePath) {
        // resourcePath must start with '/'
        // to specify the root resource only, resourcePath should be an empty string
        if (resourcePath.equals("/")) {
            resourcePath = "";
        }
        String resource =
            resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;

        statement.addResource(String
            .format(EXECUTE_API_ARN_FORMAT, region, awsAccountId, restApiId, stage,
                httpMethod.toString(),
                resource));
    }
}