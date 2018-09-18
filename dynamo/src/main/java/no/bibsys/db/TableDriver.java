package no.bibsys.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;

public class TableDriver {

	private AmazonDynamoDB client;
	private transient DynamoDB dynamoDB;


	private TableDriver() {
	}

	public static TableDriver create() {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
		DynamoDB dynamoDB = new DynamoDB(client);
		return create(client, dynamoDB);
	}

	public static TableDriver create(final AmazonDynamoDB client, final DynamoDB dynamoDB) {
		if(client == null) {
			throw new IllegalStateException("Cannot set null client ");  
		}
		TableDriver tableDriver = new TableDriver(client, dynamoDB);
		return tableDriver;
	}


	private TableDriver(final AmazonDynamoDB client, final DynamoDB dynamoDB) {
		this.client = client;
		this.dynamoDB = dynamoDB;
	}

	public AmazonDynamoDB getClient() {
		return client;
	}

	public DynamoDB getDynamoDB() {
		return dynamoDB;
	}

	public Table getTable(final String tableName) {
		return dynamoDB.getTable(tableName);

	}
}
