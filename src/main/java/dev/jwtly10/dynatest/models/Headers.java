package dev.jwtly10.dynatest.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Headers {
    private Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public String getHeader(String key) {
        return headers.get(key);
    }

    @JsonAnySetter
    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }
}