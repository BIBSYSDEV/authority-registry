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
    private final ObjectMapper jsonMapper = JsonUtils.newJsonParser();


    @Test
    public void constrcutor_effect_action_starResources_nonEmptyJsonString() throws IOException {
        Statement statement = new Statement(Statement.ALLOW_EFFECT, "service:action",
            Statement.ALL_RESOURCES, Collections.EMPTY_MAP);
        String json = statement.toJson();
        JsonNode rootNode = jsonMapper.readTree(json);

        String effect = rootNode.get(EFFECT).asText();
        assertThat(effect, is(equalTo(Statement.ALLOW_EFFECT)));

        JsonNode action = rootNode.get(ACTION);

        assertTrue(action.isArray());
        assertThat(action.get(0).asText(), is(equalTo("service:action")));

        String resource = rootNode.get(RESOURCE).asText();
        assertThat(resource, is(equalTo(Statement.ALL_RESOURCES)));
    }

    @Test
    public void constrcutor_effectActionResourcesList_nonEmptyJsonString() throws IOException {
        List<String> resourceList = new ArrayList<>();
        resourceList.add("resource1");
        resourceList.add("resource2");

        Statement statement = new Statement(Statement.ALLOW_EFFECT, "service:action", resourceList
            , Collections.EMPTY_MAP);
        String json = statement.toJson();

        JsonNode rootNode = jsonMapper.readTree(json);

        JsonNode resources = rootNode.get(RESOURCE);

        assertTrue(resources.isArray());
        assertThat(resources.get(0).asText(), is(equalTo("resource1")));
        assertThat(resources.get(1).asText(), is(equalTo("resource2")));
    }


}
