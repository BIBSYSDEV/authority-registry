package no.bibsys.authorization;

import java.util.Collections;
import org.junit.Test;

public class PolicyDocumentTest {


    public static final String REGION = "Region";
    public static final String AWS_ACCOUNT_ID = "AwsAccountId";
    public static final String REST_API_ID = "RESTAPIID";
    public static final String STAGE = "Stage";

    @Test
    public void test() {
        PolicyDocument policyDocument = new PolicyDocument(REGION, AWS_ACCOUNT_ID, REST_API_ID,
            STAGE);

        policyDocument.addStatement(sampleStatement());
        policyDocument

    }


    private Statement sampleStatement() {
        return new Statement(Statement.ALLOW_EFFECT,
            Statement.ACTION_API_INVOKE,
            Statement.ALL_RESOURCES, Collections.emptyMap());

    }

}
