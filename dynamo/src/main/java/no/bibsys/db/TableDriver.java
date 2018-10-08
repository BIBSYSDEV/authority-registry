package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;

public final class TableDriver {

    private transient AmazonDynamoDB client;
    private transient DynamoDB dynamoDb;


    private TableDriver() {
    }

    private TableDriver(final AmazonDynamoDB client, final DynamoDB dynamoDb) {
        this.client = client;
        this.dynamoDb = dynamoDb;
    }


    /**
     * Create custom connection with DynamoDB.
     *
     * @return customized TableDriver
     */
    public static TableDriver create(final AmazonDynamoDB client, final DynamoDB dynamoDb) {
        if (client == null) {
            throw new IllegalStateException("Cannot set null client ");
        }
        TableDriver tableDriver = new TableDriver(client, dynamoDb);
        return tableDriver;
    }

    public AmazonDynamoDB getClient() {
        return client;
    }

    public DynamoDB getDynamoDb() {
        return dynamoDb;
    }

    public Table getTable(final String tableName) {
        return dynamoDb.getTable(tableName);

    }


    public void deleteTable(final String tableName) {
        client.deleteTable(tableName);

    }
}
