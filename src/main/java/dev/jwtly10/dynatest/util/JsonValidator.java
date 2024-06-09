package dev.jwtly10.dynatest.util;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Component;

@Component
public class JsonValidator {
    public void validate(String jsonSchema, String json) throws ValidationException {
        JSONObject rawSchema = new JSONObject(new JSONTokener(jsonSchema));
        Schema schema = SchemaLoader.load(rawSchema);
        schema.validate(new JSONObject(json));
    }
}