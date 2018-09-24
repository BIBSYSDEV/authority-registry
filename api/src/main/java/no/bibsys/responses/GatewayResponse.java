package no.bibsys.responses;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * DO NOT USE WITH SPRINGBOOT POJO containing response object for API Gateway. This can be used when
 * SpringBoot is NOT used
 */
public class GatewayResponse {

    private String body;
    private Map<String, String> headers;
    private int statusCode;


    public GatewayResponse() {
    }


    public GatewayResponse(final String body, final Map<String, String> headers,
        final int statusCode) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
    }

    public GatewayResponse(final String body) {
        this.body = body;
        HashMap<String, String> map = defaultHeaders();
        this.headers = Collections.unmodifiableMap(map);
        this.statusCode = 200;
    }


    public static HashMap<String, String> defaultHeaders() {
        HashMap<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        return map;
    }


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}