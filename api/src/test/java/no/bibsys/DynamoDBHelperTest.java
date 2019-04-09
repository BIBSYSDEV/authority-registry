package no.bibsys;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

public class DynamoDBHelperTest {

    @Test
    public void getClient_withStandardClient_nonNullClient() {
        AmazonDynamoDB client = DynamoDBHelper.getClient();
        
        assertThat(client, not(nullValue()));
    }

}
