package no.bibsys.authorization;

import com.fasterxml.jackson.annotation.JsonValue;

public class Resource {

    public static final Character PATH_SEPARATOR_CHAR = '/';
    private static final String EXECUTE_API_ARN_FORMAT =
        "arn:aws:execute-api:{regionId}:{accountId}:{appId}" + PATH_SEPARATOR_CHAR
            + "{stage}/{httpVerb}/{resource}";
    public static Resource ANY_RESOURCE = anyResource();
    private transient String region;
    private transient String awsAccountId;
    private transient String restApiId;
    private transient String stage;
    private transient HttpMethod httpMethod;
    private transient String resourcePath;
    private transient final String resourceString;


    public Resource(String region, String awsAccountId, String restApiId, String stage,
        HttpMethod httpMethod, String resourcePath) {
        this.region = region;
        this.awsAccountId = awsAccountId;
        this.restApiId = restApiId;
        this.stage = stage;
        this.httpMethod = httpMethod;
        this.resourcePath = resourcePath;
        this.resourceString = formatResourceString();
    }


    private Resource(String resourceString) {
        this.resourceString = resourceString;
    }

    private static Resource anyResource() {
        return new Resource("*");
    }

    @JsonValue
    @Override
    public String toString() {
        return resourceString;
    }


    private String formatResourceString() {

        String resource = removeFirstSlashFromResourcePath(resourcePath);
        String result = EXECUTE_API_ARN_FORMAT
            .replaceFirst("\\{regionId\\}", region)
            .replaceFirst("\\{accountId\\}", awsAccountId)
            .replaceFirst("\\{appId\\}", restApiId)
            .replaceFirst("\\{stage\\}", stage)
            .replaceFirst("\\{httpVerb\\}", httpMethod.toString())
            .replaceFirst("\\{resource\\}", resource);
        return result;


    }

    private String removeFirstSlashFromResourcePath(String resourcePath) {
        // resourcePath must start with '/'
        // to specify the root resource only, resourcePath should be an empty string

        if (PATH_SEPARATOR_CHAR.toString().equals(resourcePath)) {
            return "";
        } else if (resourcePath.charAt(0) == PATH_SEPARATOR_CHAR) {
            return resourcePath.substring(1);
        } else {
            return resourcePath;
        }

    }


    @Override
    public boolean equals(Object resource) {
        if (resource instanceof Resource) {
            Resource that = (Resource) resource;
            return this.toString().equals(that.toString());
        } else {
            return false;
        }

    }


    @Override
    public int hashCode() {
        return this.resourceString.hashCode();
    }


}
