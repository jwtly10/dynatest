package dev.jwtly10.dynatest.models;

import dev.jwtly10.dynatest.enums.Method;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.net.URL;
import java.util.Map;

@Data
@Builder
@Jacksonized
public class Request {
    private Method method;
    private String url;
    private Headers headers;
    private QueryParams queryParams;
    private Body body;

    public String getHeader(String key) {
        return headers.getHeader(key);
    }

    public void setHeader(String key, String value) {
        headers.setHeader(key, value);
    }

    public Map<String, String> getAllHeaders() {
        return headers.getHeaders();
    }

    public Object getParam(String key) {
        return queryParams.getParam(key);
    }

    public void setParam(String key, String value) {
        queryParams.setParam(key, value);
    }

    public Map<String, Object> getAllParams() {
        return queryParams.getParams();

    }
}