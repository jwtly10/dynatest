package dev.jwtly10.dynatest.models;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

@Data
@Builder
@Jacksonized
public class Response {
    private int statusCode;
    private Headers headers;
    private StoreValues storeValues;

    public String getHeader(String key) {
        return headers.getHeader(key);
    }

    public void setHeader(String key, String value) {
        headers.setHeader(key, value);
    }

    public Map<String, String> getAllHeaders() {
        return headers.getHeaders();
    }

    public String getStoredValue(String key) {
        return storeValues.getValue(key);
    }

    public void setStoredValue(String key, String value) {
        storeValues.setValue(key, value);
    }

    public Map<String, String> getAllStoredValues() {
        return storeValues.getValues();
    }
}