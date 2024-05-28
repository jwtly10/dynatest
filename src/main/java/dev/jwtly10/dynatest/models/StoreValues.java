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
public class StoreValues {
    private Map<String, String> values = new HashMap<>();

    public String getValue(String key) {
        return values.get(key);
    }

    @JsonAnySetter
    public void setValue(String key, String value) {
        values.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, String> getValues() {
        return new HashMap<>(values);
    }
}