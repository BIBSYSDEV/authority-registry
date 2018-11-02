package no.bibsys;

import java.util.Optional;
import no.bibsys.web.security.ApiKeyConstants;

public class MockEnvironmentReader extends EnvironmentReader {
    
    public static final String TEST_API_ADMIN_API_KEY = "testApiAdminApiKey";
    public static final String TEST_REGISTRY_ADMIN_API_KEY = "testRegistryAdminApiKey";
    
    @Override
    public Optional<String> getEnvForName(String name) {
        if (ApiKeyConstants.API_ADMIN_API_KEY.equals(name)) {
            return Optional.ofNullable(TEST_API_ADMIN_API_KEY);
        } else if (ApiKeyConstants.REGISTRY_ADMIN_API_KEY.equals(name)) {
            return Optional.ofNullable(TEST_REGISTRY_ADMIN_API_KEY);
        }
        
        return Optional.empty();
    }
    
}
