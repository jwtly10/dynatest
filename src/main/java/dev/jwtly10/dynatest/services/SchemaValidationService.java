package dev.jwtly10.dynatest.services;

import dev.jwtly10.dynatest.util.JsonValidator;
import org.everit.json.schema.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class SchemaValidationService {

    private final JsonValidator validator;

    public SchemaValidationService(JsonValidator validator) {
        this.validator = validator;
    }

    public void validateSchema(String expectedSchema, String actualJson) throws ValidationException {
        validator.validate(expectedSchema, actualJson);
    }
}