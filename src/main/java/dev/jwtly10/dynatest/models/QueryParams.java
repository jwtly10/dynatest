package dev.jwtly10.dynatest.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryParams {
    private Map<String, String> params = new HashMap<>();

    public Object getParam(String key) {
        return params.get(key);
    }

    @JsonAnySetter
    public void setParam(String key, String value) {
        params.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getParams() {
        return new HashMap<>(params);
    }
}