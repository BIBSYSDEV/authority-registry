package no.bibsys;

import java.util.Optional;

public class MockEnvironmentReader extends EnvironmentReader {
    
    public static final String TEST_API_ADMIN_API_KEY = "testApiAdminApiKey";
    public static final String TEST_REGISTRY_ADMIN_API_KEY = "testRegistryAdminApiKey";
    public static final String API_KEYS_FOR_UNIT_TESTS = "apiKeysForUnitTests";
    
    @Override
    public Optional<String> getEnvForName(String name) {
        if (EnvironmentReader.API_KEY_TABLE_NAME.equals(name)) {
            return Optional.ofNullable(API_KEYS_FOR_UNIT_TESTS);
        }
        
        return Optional.empty();
    }
    
}
