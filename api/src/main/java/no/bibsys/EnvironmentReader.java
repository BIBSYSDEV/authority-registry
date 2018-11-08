package no.bibsys;

import java.util.Optional;

public class EnvironmentReader {

    private final static String STAGE_NAME = "STAGE_NAME";
    
    public Optional<String> getEnvForName(String name) {
        return Optional.ofNullable(System.getenv(name));
    }

    public String getStageName() {
        return getEnvForName(STAGE_NAME).orElse("");
    }
}