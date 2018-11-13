package no.bibsys.db;

public abstract class DynamoTest {

    String tableName = "itemTable";
    TestTemplate template = new TestTemplate(tableName);

    public abstract void init();


}
