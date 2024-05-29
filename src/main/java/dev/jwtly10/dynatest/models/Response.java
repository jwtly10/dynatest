package dev.jwtly10.dynatest.models;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@Jacksonized
public class Response {
    private int statusCode;
    private Headers headers;
    private JsonBody jsonBody;

    public String getHeader(String key) {
        return headers.getHeader(key);
    }

    public void setHeader(String key, String value) {
        headers.setHeader(key, value);
    }

    public Map<String, String> getHeaders() {
        if (headers == null) {
            return new HashMap<>();
        }
        return headers.getHeaders();
    }


    public Map<String, Object> getJsonBody() {
        if (jsonBody == null) {
            return new HashMap<>();
        }
        return jsonBody.getBodyData();
    }
}