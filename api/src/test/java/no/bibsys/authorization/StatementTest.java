package no.bibsys.authorization;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.bibsys.aws.tools.JsonUtils;
import org.junit.Test;

public class StatementTest {


    private static final String RESOURCE = "Resource";
    private static final String EFFECT = "Effect";
    private static final String ACTION = "Action";
    public static final String REGION = "eu-west-1";
    public static final String AWS_ACCOUNT_ID = "ACCOUNT_ID";
    public static final String REST_API_ID = "REST_API_ID";
    public static final String STAGE = "test";
    private final ObjectMapper jsonMapper = JsonUtils.newJsonParser();


    @Test
    public void constrcutor_effect_action_starResources_nonEmptyJsonString() throws IOException {
        Statement statement = new Statement(Statement.ALLOW_EFFECT,
            Collections.singletonList("service:action"),
            Resource.ANY_RESOURCE,
            Collections.emptyMap());
        String json = JsonUtils.newJsonParser().writeValueAsString(statement);
        JsonNode rootNode = jsonMapper.readTree(json);

        String effect = rootNode.get(EFFECT).asText();
        assertThat(effect, is(equalTo(Statement.ALLOW_EFFECT)));

        JsonNode action = rootNode.get(ACTION);

        assertTrue(action.isArray());
        assertThat(action.get(0).asText(), is(equalTo("service:action")));

        String resource = rootNode.get(RESOURCE).asText();
        assertThat(resource, is(equalTo("*")));
    }

    @Test
    public void constrcutor_effectActionResourcesList_nonEmptyJsonString() throws IOException {
        List<Resource> resourceList = new ArrayList<>();
        resourceList.add(new Resource(REGION, AWS_ACCOUNT_ID, REST_API_ID, STAGE,
            HttpMethod.GET, "resource1"));
        resourceList.add(new Resource(REGION, AWS_ACCOUNT_ID, REST_API_ID, STAGE,
            HttpMethod.GET, "resource2"));

        Statement statement = new Statement(Statement.ALLOW_EFFECT,
            Collections.singletonList("service:action"),
            resourceList
            , Collections.EMPTY_MAP);
        String json = JsonUtils.newJsonParser().writeValueAsString(statement);

        JsonNode rootNode = jsonMapper.readTree(json);

        JsonNode resources = rootNode.get(RESOURCE);

        assertTrue(resources.isArray());
        assertThat(resources.get(0).asText(), is(equalTo(resourceList.get(0).toString())));
        assertThat(resources.get(1).asText(), is(equalTo(resourceList.get(1).toString())));
    }


}
