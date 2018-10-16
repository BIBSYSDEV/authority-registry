package no.bibsys.web.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;

public class SimpleResponse {

    private String message;


    public SimpleResponse() {}


    public SimpleResponse(String message) {
        setMessage(message);
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SimpleResponse)) {
            return false;
        }

        SimpleResponse that = (SimpleResponse) object;

        return message != null ? message.equals(that.message) : that.message == null;
    }

    @Override
    public int hashCode() {
        return message != null ? message.hashCode() : 0;
    }


    public String getMessage() {
        return message;
    }

    public final void setMessage(String message) {
        this.message = message;
    }

    public String toGatewayResponse() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, String> jsonObject = new HashMap<>();
        jsonObject.put("message", getMessage());
        String body = mapper.writeValueAsString(jsonObject);
        GatewayResponse response = new GatewayResponse(body);
        return mapper.writeValueAsString(response);

    }


}
