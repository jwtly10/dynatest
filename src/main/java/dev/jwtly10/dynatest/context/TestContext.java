package dev.jwtly10.dynatest.context;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Data
public class TestContext {
    private Map<String, String> variables = new HashMap<>();
    private Map<String, String> envVariables = new HashMap<>();

    public TestContext() {
        // TODO: Initialise environment vars
    }

    public void setVariable(String key, String value) {
        variables.put(key, value);
    }

    public void setEnvVariable(String key, String value) {
        envVariables.put(key, value);
    }

    public String getValue(String key) throws RuntimeException {
        // Check if this is a scoped variable or environment var
        String val = variables.getOrDefault(key, envVariables.get(key));

        if (val == null) {
            throw new RuntimeException("No value in context for key: " + key);
        }

        return val;
    }
}