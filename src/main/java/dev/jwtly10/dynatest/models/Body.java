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
    private Map<String, Object> data = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public Object getKey(String key) {
        return data.get(key);
    }

    @JsonAnySetter
    public void setData(String key, Object value) {
        if (value instanceof Map) {
            flattenMap(key, (Map<String, Object>) value);
        } else {
            data.put(key, value);
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
                data.put(newKey, entry.getValue());
            }
        }
    }

    @JsonAnyGetter
    public Map<String, Object> getData() {
        return new HashMap<>(data);
    }
}