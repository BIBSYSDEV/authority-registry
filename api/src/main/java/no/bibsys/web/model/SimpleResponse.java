package no.bibsys.web.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;

import javax.ws.rs.core.Response.Status;

public class SimpleResponse {

    private String message;
    private int statusCode;
    private Status status;

    public SimpleResponse() {}
    
    
    public SimpleResponse(String message) {
        setMessage(message);
        setStatusCode(200);
        setStatus(Status.OK);
    }
    
    public SimpleResponse(String message, Status status) {
        setMessage(message);
        setStatusCode(status.getStatusCode());
        setStatus(status);
    }

    public final Status getStatus() {
        return status;
    }


    public final void setStatus(Status status) {
        this.status = status;
    }

    
    public String toGatewayResponse() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, String> jsonObject = new HashMap<>();
        jsonObject.put("message", getMessage());
        String body = mapper.writeValueAsString(jsonObject);
        GatewayResponse response = new GatewayResponse(body, new HashMap<>(), statusCode);
        return mapper.writeValueAsString(response);

    }

    public int getStatusCode() {
        return statusCode;
    }
    
    public final void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    
    public String getMessage() {
        return message;
    }

    public final void setMessage(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        return "SimpleResponse [message=" + message + ", statusCode=" + statusCode + ", status=" + status + "]";
    }

}
