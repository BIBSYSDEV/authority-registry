package no.bibsys;

import com.google.common.base.Preconditions;

public class EnvironmentReader {

    public static final String STAGE_NAME = "STAGE_NAME";
    public static final String API_KEY_TABLE_NAME = "API_KEY_TABLE_NAME";
    public static final String VALIDATION_SCHEMA_TABLE_NAME = "VALIDATION_SCHEMA_TABLE_NAME";

    public String getEnvForName(String name) {
        String value = System.getenv(name);
        Preconditions.checkNotNull(value);
        return value;
    }
}
