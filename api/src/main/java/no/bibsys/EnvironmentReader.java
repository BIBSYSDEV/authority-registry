package no.bibsys;

import java.util.Optional;

public class EnvironmentReader {

    public Optional<String> getEnvForName(String name) {
        return Optional.ofNullable(System.getenv(name));
    }
}