package no.bibsys.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.bibsys.aws.tools.JsonUtils;

public class Statement {


    public static final String ALLOW_EFFECT = "Allow";
    public static final String DENY_EFFECT = "Deny";
    public static final String ALL_RESOURCES = "*";
    public static final String ACTION_API_INVOKE = "execute-api:Invoke";
    public static final Map<String, Map<String, Object>> EMPTY_CONDITIONS = Collections.emptyMap();

    private static final ObjectMapper jsonMapper = JsonUtils.newJsonParser();

    @JsonProperty("Effect")
    private String effect;

    @JsonProperty("Action")
    private List<String> action;

    @JsonProperty("Condition")
    private Map<String, Map<String, Object>> condition;

    @JsonProperty("Resource")
    private Object resourceList;


    public Statement(String effect, String action, String resourceList,
        Map<String, Map<String, Object>> condition) {
        this(effect, Collections.singletonList(action), null, condition);
        this.resourceList = resourceList;

    }


    public Statement(String effect, String action, List<String> resourceList,
        Map<String, Map<String, Object>> condition) {
        this(effect, Collections.singletonList(action), resourceList, condition);

    }

    public Statement(String effect, List<String> action, List<String> resourceList,
        Map<String, Map<String, Object>> condition) {
        this.effect = effect;
        this.action = action;
        this.resourceList = resourceList;
        this.condition = condition;
    }

    public static Statement getEmptyInvokeStatement(String effect) {
        return new Statement(effect, ACTION_API_INVOKE, new ArrayList<>(), new HashMap<>());
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public List<String> getAction() {
        return action;
    }

    public void setAction(List<String> action) {
        this.action = action;
    }


    private boolean isResourceListValid() {
        if (resourceList instanceof String &&
            ((String) resourceList).equals(ALL_RESOURCES)
        ) {
            return true;
        } else if (resourceList instanceof List
            && !(((List) resourceList).isEmpty())) {
            Object item = ((List) resourceList).get(0);
            if (item instanceof String) {
                return true;
            }
        }
        return false;
    }


    public void addResource(String resource) {
        if (this.resourceList instanceof List) {
            ((List) resourceList).add(resource);
        } else {
            throw new IllegalStateException("Trying to add a list element in a non-list object");
        }
    }

    public Map<String, Map<String, Object>> getCondition() {
        return condition;
    }

    public void addCondition(String operator, String key, Object value) {
        condition.put(operator, Collections.singletonMap(key, value));
    }

    public String toJson() throws JsonProcessingException {
        return jsonMapper.writeValueAsString(this);
    }

}
