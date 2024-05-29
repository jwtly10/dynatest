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
    private Body body;
    private StoreValues storeValues;

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

    public String getStoredValue(String key) {
        return storeValues.getValue(key);
    }

    public void setStoredValue(String key, String value) {
        storeValues.setValue(key, value);
    }

    public Map<String, String> getStoredValues() {
        if (storeValues == null) {
            return new HashMap<>();
        }
        return storeValues.getValues();
    }

    public Map<String, Object> getBody() {
        if (body == null) {
            return new HashMap<>();
        }
        return body.getData();
    }
}