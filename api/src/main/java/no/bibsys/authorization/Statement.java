package no.bibsys.authorization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Statement {


    public static final String ALLOW_EFFECT = "Allow";
    public static final String DENY_EFFECT = "Deny";
    public static final String ACTION_API_INVOKE = "execute-api:Invoke";
    public static final Map<String, Map<String, Object>> EMPTY_CONDITIONS = Collections.emptyMap();
    @JsonProperty("Condition")
    @JsonInclude(Include.NON_EMPTY)
    private final transient Map<String, Map<String, Object>> condition;
    @JsonProperty("Resource")
    private final transient Object resourceList;
    @JsonProperty("Effect")
    private String effect;
    @JsonProperty("Action")
    private List<String> action;


    public Statement(String effect, List<String> actions, Resource resource,
        Map<String, Map<String, Object>> condition) {
        this.effect = effect;
        this.action = actions;
        this.condition = condition;
        this.resourceList = resource;

    }

    public Statement(String effect, List<String> action, List<Resource> resourceList,
        Map<String, Map<String, Object>> condition) {
        this.effect = effect;
        this.action = action;
        this.resourceList = resourceList;
        this.condition = condition;
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

    public Map<String, Map<String, Object>> getCondition() {
        return condition;
    }


    public void addResource(String resource) {
        if (this.resourceList instanceof List) {
            ((List) resourceList).add(resource);
        } else {
            throw new IllegalStateException("Trying to add a list element in a non-list object");
        }
    }


    public void addCondition(String operator, String key, Object value) {
        condition.put(operator, Collections.singletonMap(key, value));
    }


}
