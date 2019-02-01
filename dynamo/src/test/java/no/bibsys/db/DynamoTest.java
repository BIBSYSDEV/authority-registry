package no.bibsys.db;

import java.io.IOException;

public abstract class DynamoTest {

    String registryMetadataTableName = "validationSchemas";
    String tableName = "tableTable";

    public abstract void init() throws IOException;


}
