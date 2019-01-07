package no.bibsys.authorization;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import no.bibsys.aws.tools.JsonUtils;
import org.junit.Test;

public class PolicyDocumentTest extends AccessPolicyTest {




    @Test
    public void acceptAll_RegionAccountApiIdStage_acceptAllActionsPolicyDocument()
        throws IOException {
        PolicyDocument policyDocument = PolicyDocument.getAllowAllPolicy(
            REGION,
            AWS_ACCOUNT_ID,
            REST_API_ID, STAGE);

        String json = JsonUtils.newJsonParser().writeValueAsString(policyDocument);
        ObjectMapper jsonParser = JsonUtils.newJsonParser();
        JsonNode root = jsonParser.readTree(json);

        assertTrue(root.has(VERSION_FIELD));
        assertTrue(root.has(STATEMENT_FIELD));
        assertTrue(root.get(STATEMENT_FIELD).get(0).has(ACTION_FIELD));
        assertTrue(root.get(STATEMENT_FIELD).get(0).has(EFFECT_FIELD));
        assertTrue(root.get(STATEMENT_FIELD).get(0).has(RESOURCE_FIELD));

        assertThat(root.get(STATEMENT_FIELD).get(0).get(EFFECT_FIELD).asText(),
            is(equalTo(Statement.ALLOW_EFFECT)));


    }


    @Test
    public void denyAll_RegionAccountApiIdStage_denyAllActionsPolicyDocument()
        throws IOException {
        PolicyDocument policyDocument = PolicyDocument.getDenyAllPolicy(REGION,
            AWS_ACCOUNT_ID, REST_API_ID, STAGE);

        String json = JsonUtils.newJsonParser().writeValueAsString(policyDocument);
        ObjectMapper jsonParser = JsonUtils.newJsonParser();
        JsonNode root = jsonParser.readTree(json);

        assertTrue(root.has(VERSION_FIELD));
        assertTrue(root.has(STATEMENT_FIELD));
        assertTrue(root.get(STATEMENT_FIELD).get(0).has(ACTION_FIELD));
        assertTrue(root.get(STATEMENT_FIELD).get(0).has(EFFECT_FIELD));
        assertTrue(root.get(STATEMENT_FIELD).get(0).has(RESOURCE_FIELD));

        assertThat(root.get(STATEMENT_FIELD).get(0).get(EFFECT_FIELD).asText(),
            is(equalTo(Statement.DENY_EFFECT)));



    }


    private Statement sampleStatement() {
        return new Statement(Statement.ALLOW_EFFECT,
            Collections.singletonList(Statement.ACTION_API_INVOKE),
            Resource.ANY_RESOURCE, Collections.emptyMap());


    }

}
