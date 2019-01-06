package no.bibsys.authorization;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Collections;
import no.bibsys.aws.tools.JsonUtils;
import org.junit.Test;

public class PolicyDocumentTest {


    public static final String REGION = "Region";
    public static final String AWS_ACCOUNT_ID = "AwsAccountId";
    public static final String REST_API_ID = "RESTAPIID";
    public static final String STAGE = "Stage";
    public static final String PRINCIPAL_ID="principalId";

    @Test
    public void test() {
        PolicyDocument policyDocument = new PolicyDocument(PRINCIPAL_ID,
            REGION,
            AWS_ACCOUNT_ID,
            REST_API_ID,
            STAGE);
        policyDocument.addStatement(sampleStatement());


    }



    @Test
    public void acceptAll_RegionAccountApiIdStage_acceptAllActionsPolicyDocument()
        throws JsonProcessingException {
        PolicyDocument policyDocument = PolicyDocument.getAllowAllPolicy(PRINCIPAL_ID,
            REGION,
            AWS_ACCOUNT_ID,
            REST_API_ID,STAGE);

        String json= JsonUtils.newJsonParser().writeValueAsString(policyDocument);

        assertThat(policyDocument,is(not(equalTo(null))));

    }


    private Statement sampleStatement() {
        return new Statement(Statement.ALLOW_EFFECT,
            Collections.singletonList(Statement.ACTION_API_INVOKE),
            Resource.ANY_RESOURCE, Collections.emptyMap());


    }

}
