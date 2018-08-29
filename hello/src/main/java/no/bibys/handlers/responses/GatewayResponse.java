package no.bibys.handlers.responses;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * POJO containing response object for API Gateway.
 */
public class GatewayResponse<O> {

    private  O body;
    private  Map<String, String> headers;
    private  int statusCode;



    public GatewayResponse(){};

    public GatewayResponse(final O body, final Map<String, String> headers, final int statusCode) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
    }

    public GatewayResponse(final O body){
        this.body=body;
        HashMap<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        this.headers=Collections.unmodifiableMap(map);
        this.statusCode=200;
    }


    public O getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setBody(O body) {
        this.body = body;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
