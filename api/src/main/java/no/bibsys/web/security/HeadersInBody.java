package no.bibsys.web.security;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HeadersInBody {
    
    private Map<String,String> headers;

    public HeadersInBody() {
        headers = new HashMap<>();
    }
    
    public Map<String,String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String,String> headers) {
        this.headers = headers;
    }

    
}
