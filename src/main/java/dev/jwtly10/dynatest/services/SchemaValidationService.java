package dev.jwtly10.dynatest.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.jwtly10.dynatest.exceptions.SchemaValidationException;
import dev.jwtly10.dynatest.models.ExpectedResponse;
import dev.jwtly10.dynatest.models.Response;
import dev.jwtly10.dynatest.parser.JsonParser;
import dev.jwtly10.dynatest.util.JsonValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SchemaValidationService {

    private final JsonValidator validator;

    public SchemaValidationService(JsonValidator validator) {
        this.validator = validator;
    }

    public void validateResponse(Response response, ExpectedResponse expected) throws SchemaValidationException {
        if (response.getStatusCode() != expected.getStatusCode()) {
            throw new SchemaValidationException("Response code mismatch: " + response.getStatusCode() + "!=" + expected.getStatusCode());
        }

        if (expected.getValidationSchema() != null) {
            String schemaString;
            try {
                schemaString = JsonParser.toJson(expected.getValidationSchema());
            } catch (JsonProcessingException e) {
                log.error("This shouldn't happen - unable to parse validation schema from ExpectedJson object", e);
                throw new SchemaValidationException("Unable to parse json from validation schema", e);
            }

            try {
                validateSchema(schemaString, response.getRawBody());
            } catch (Exception e) {
                throw new SchemaValidationException("Failed to validate the schema: " + e.getMessage());
            }
        }
    }

    public void validateSchema(String expectedSchema, String actualJson) throws Exception {
        log.info(expectedSchema);
        log.info(actualJson);
        validator.validate(expectedSchema, actualJson);
    }
}