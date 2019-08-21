package no.bibsys;

import com.google.common.base.Preconditions;
import no.bibsys.aws.tools.Environment;

public class MockEnvironment extends Environment {

    public static final String TEST_API_ADMIN_API_KEY = "testApiAdminApiKey";
    public static final String TEST_REGISTRY_ADMIN_API_KEY = "testRegistryAdminApiKey";
    public static final String API_KEYS_FOR_UNIT_TESTS = "apiKeysForUnitTests";

    
    
    @Override
    public String readEnv(String name) {
        String value = null;

        if (EnvironmentVariables.API_KEY_TABLE_NAME.equals(name)) {
            value = API_KEYS_FOR_UNIT_TESTS;
        } else if (EnvironmentVariables.STAGE_NAME.equals(name)) {
            value = "test";
        } else if (EnvironmentVariables.REGISTRY_METADATA_TABLE_NAME.equals(name)) {
            value = "unitTestsValidationSchemaTable";
        } else if (EnvironmentVariables.CLOUDSEARCH_SEARCH_ENDPOINT.equals(name)) {
            value = "cloudsearch";
        }

        Preconditions.checkNotNull(value);

        return value;
    }
}
