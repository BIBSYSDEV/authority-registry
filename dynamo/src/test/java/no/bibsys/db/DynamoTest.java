package no.bibsys.db;

import no.bibsys.db.structures.EntityRegistryTemplate;

public abstract class DynamoTest {

    String tableName = "itemTable";
    EntityRegistryTemplate template = new EntityRegistryTemplate(tableName);

    public abstract void init();


}
