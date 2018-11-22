package no.bibsys;

import com.google.common.base.Preconditions;

public class MockEnvironmentReader extends EnvironmentReader {
    
    public static final String TEST_API_ADMIN_API_KEY = "testApiAdminApiKey";
    public static final String TEST_REGISTRY_ADMIN_API_KEY = "testRegistryAdminApiKey";
    public static final String API_KEYS_FOR_UNIT_TESTS = "apiKeysForUnitTests";
    
    @Override
    public String getEnvForName(String name) {
        String value = null;
        
        if (EnvironmentReader.API_KEY_TABLE_NAME.equals(name)) {
            value = API_KEYS_FOR_UNIT_TESTS;
        } else if (EnvironmentReader.STAGE_NAME.equals(name)) {
        	value = "test";
        } else if (EnvironmentReader.VALIDATION_SCHEMA_TABLE_NAME.equals(name)) {
            value = "unitTestsValidationSchemaTable";
        }
        
        Preconditions.checkNotNull(value);
        
        return value;
    }
}