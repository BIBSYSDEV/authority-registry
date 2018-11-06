package no.bibsys.web.security;

public class ApiKeyConstants {

    public static final String API_ADMIN_API_KEY = "API_ADMIN_API_KEY";
    public static final String REGISTRY_ADMIN_API_KEY = "REGISTRY_ADMIN_API_KEY";
    
    public static final String API_KEY = "apiKey";
    
    /**
     * Name of the header field that contains the API key. Must be named this way to be recognised during import to API Gateway.
     */
    public static final String API_KEY_PARAM_NAME = "x-api-key";
    
    /**
     * Name of the env variable that holds the name of the table that holds API keys.
     */
    public static final String API_KEY_TABLE_NAME = "API_KEY_TABLE_NAME";

    
}
