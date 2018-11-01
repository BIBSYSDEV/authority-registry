package no.bibsys;

import java.util.Optional;

public class MockEnvironmentReader extends EnvironmentReader {
    
    public static final String TEST_API_ADMIN_API_KEY = "testAdminApiKey";
    
    @Override
    public Optional<String> getEnvForName(String name) {
        if (API_ADMIN_API_KEY.equals(name)) {
            return Optional.ofNullable(TEST_API_ADMIN_API_KEY);
        }
        
        return Optional.empty();
    }
    
}
