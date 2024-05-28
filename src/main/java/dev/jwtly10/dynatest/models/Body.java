package dev.jwtly10.dynatest.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private Map<String, String> keys = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public String GetKey(String key) {
        return keys.get(key);
    }

    @JsonAnySetter
    public void setKey(String key, String value) {
        keys.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, String> getKeys() {
        return new HashMap<>(keys);
    }
}