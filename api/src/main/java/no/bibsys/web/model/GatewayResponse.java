package no.bibsys.web.model;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DO NOT USE WITH SPRINGBOOT!! POJO containing response object for API Gateway. This can be used when
 * SpringBoot is NOT used
 */
public class GatewayResponse {

    private String body;
    private Map<String, String> headers;
    private int statusCode;


    public GatewayResponse() {}


    public GatewayResponse(final String body, final Map<String, String> headers, final int statusCode) {
        this.statusCode = statusCode;
        this.body = body;

        this.headers = Collections.unmodifiableMap(defaultHeaders());
    }
    
    public GatewayResponse(final String body, final int statusCode){
        this(body, defaultHeaders(),statusCode);
    }

public GatewayResponse(final String body) {
    this.body = body;
    Map<String, String> map = defaultHeaders();
    this.headers = Collections.unmodifiableMap(map);
    this.statusCode = 200;
}


public static Map<String, String> defaultHeaders() {
    Map<String, String> map = new ConcurrentHashMap<>();
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
