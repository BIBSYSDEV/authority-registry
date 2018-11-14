package no.bibsys;

import java.util.Optional;

public class EnvironmentReader {

    public final static String STAGE_NAME = "STAGE_NAME";
    public static final String API_KEY_TABLE_NAME = "API_KEY_TABLE_NAME";
    
    public Optional<String> getEnvForName(String name) {
        return Optional.ofNullable(System.getenv(name));
    }
}