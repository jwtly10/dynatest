package dev.jwtly10.dynatest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.jwtly10.dynatest.enums.Method;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@Jacksonized
public class Request {
    @NotNull(message = "Method cannot be empty")
    private Method method;
    @NotNull(message = "URL cannot be empty")
    private String url;
    @JsonProperty("headers")
    private Headers requestHeaders;
    private QueryParams queryParams;
    @JsonProperty("body")
    private JsonBody jsonBody;
    private StoreValues storeValues;

    public HttpMethod getMethod() {
        // Supported Methods
        return switch (method) {
            case GET -> HttpMethod.GET;
            case POST -> HttpMethod.POST;
            case PUT -> HttpMethod.PUT;
            case DELETE -> HttpMethod.DELETE;
        };
    }

    public String getHeader(String key) {
        return requestHeaders.getHeader(key);
    }

    public void setHeader(String key, String value) {
        requestHeaders.setHeader(key, value);
    }

    public Map<String, String> getHeaders() {
        if (requestHeaders == null) {
            return new HashMap<>();
        }
        return requestHeaders.getHeaders();
    }

    public Object getParam(String key) {
        return queryParams.getParam(key);
    }

    public void setParam(String key, String value) {
        queryParams.setParam(key, value);
    }

    public Map<String, String> getParams() {
        if (queryParams == null) {
            return new HashMap<>();
        }
        return queryParams.getParams();

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

    public JsonBody getBody() {
        return jsonBody;
    }

    public Map<String, Object> getJsonBody() {
        if (jsonBody == null) {
            return new HashMap<>();
        }
        return jsonBody.getBodyData();
    }
}