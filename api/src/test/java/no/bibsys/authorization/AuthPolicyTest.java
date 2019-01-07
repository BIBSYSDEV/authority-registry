package no.bibsys.authorization;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import no.bibsys.aws.tools.JsonUtils;
import org.junit.Test;

public class AuthPolicyTest extends AccessPolicyTest {

    public static final String POLICY_DOCUMENT_FIELD = "policyDocument";
    public static final String CONTEXT_FIELD = "context";

    //    private final transient String PRINCIPAL_ID=  "principalId";

    @Test
    public void authPolicy_principalIdPolicyDocEmptyContext_authPolicyDocWithoutContext()
        throws IOException {
        PolicyDocument policyDocument = PolicyDocument.getAllowAllPolicy(REGION,
            AWS_ACCOUNT_ID,
            REST_API_ID,
            STAGE);

        AuthPolicy authPolicy = new AuthPolicy(PRINCIPAL_ID, policyDocument);

        ObjectMapper parser = JsonUtils.newJsonParser();

        String json = parser.writeValueAsString(authPolicy);
        JsonNode root = parser.readTree(json);
        assertTrue(root.has(PRINCIPAL_ID_FIELD));
        assertTrue(root.has(POLICY_DOCUMENT_FIELD));
        assertFalse(root.has(CONTEXT_FIELD));

    }


}
