package no.bibsys;

import java.util.Optional;

public class EnvironmentReader {

    public final static String API_ADMIN_API_KEY = "API_ADMIN_API_KEY";
    public final static String REGISTRY_ADMIN_API_KEY = "REGISTRY_ADMIN_API_KEY";
    
    public Optional<String> getEnvForName(String name) {
        return Optional.ofNullable(System.getenv(name));
    }
    
}
