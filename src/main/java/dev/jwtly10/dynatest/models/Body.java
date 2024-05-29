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
public class Body {
    private Map<String, Object> bodyData = new HashMap<>();

    public Object getKey(String key) {
        return bodyData.get(key);
    }

    @JsonAnySetter
    public void setBodyData(String key, Object value) {
        if (value instanceof Map) {
            if (((Map<?, ?>) value).isEmpty()) {
                bodyData.put(key, new TreeMap<>());
            } else {
                flattenMap(key, (Map<String, Object>) value);
            }
        } else {
            bodyData.put(key, value);
        }
    }

    // Helper method to flatten the map
    private void flattenMap(String prefix, Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String newKey = prefix + "." + entry.getKey();
            if (entry.getValue() instanceof Map) {
                // Recurse if the value is also a Map
                flattenMap(newKey, (Map<String, Object>) entry.getValue());
            } else {
                bodyData.put(newKey, entry.getValue());
            }
        }
    }

    @JsonAnyGetter
    public Map<String, Object> getBodyData() {
        return new HashMap<>(bodyData);
    }
}