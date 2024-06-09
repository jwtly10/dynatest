package dev.jwtly10.dynatest.services;

import dev.jwtly10.dynatest.util.JsonValidator;
import org.everit.json.schema.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SchemaValidationServiceTest {

    @Test
    void testCanValidateSchema() {
        String schema = """
                {
                       "type": "object",
                       "properties": {
                         "id": {
                           "type": "string"
                         },
                         "name": {
                           "type": "string"
                         },
                         "age": {
                           "type": "number"
                         },
                         "address": {
                           "type": "object",
                           "properties": {
                             "street": {
                               "type": "string"
                             },
                             "city": {
                               "type": "string"
                             },
                             "zipcode": {
                               "type": "string"
                             }
                           },
                           "required": ["street", "city", "zipcode"]
                         },
                         "tags": {
                           "type": "array"
                         }
                       },
                       "required": ["id", "name", "age", "address"]
                 }""";

        String response = """
                {
                  "id": "12345",
                  "name": "John Doe",
                  "age": 30,
                  "address": {
                    "street": "123 Main St",
                    "city": "Anytown",
                    "zipcode": "12345"
                  },
                  "tags": ["tag1", "tag2"]
                }""";

        SchemaValidationService validator = new SchemaValidationService(new JsonValidator());

        validator.validateSchema(schema, response);
    }

    @Test
    void testThrowsWhenRequiredMissing() {
        String schema = """
                {
                       "type": "object",
                       "properties": {
                         "id": {
                           "type": "string"
                         },
                         "name": {
                           "type": "string"
                         },
                         "age": {
                           "type": "number"
                         },
                         "address": {
                           "type": "object",
                           "properties": {
                             "street": {
                               "type": "string"
                             },
                             "city": {
                               "type": "string"
                             },
                             "zipcode": {
                               "type": "string"
                             }
                           },
                           "required": ["street", "city", "zipcode"]
                         },
                         "tags": {
                           "type": "array"
                         }
                       },
                       "required": ["id", "name", "age", "address"]
                 }""";

        String response = """
                {
                  "id": "12345",
                  "age": 30,
                  "address": {
                    "street": "123 Main St",
                    "city": "Anytown",
                    "zipcode": "12345"
                  },
                  "tags": ["tag1", "tag2"]
                }""";

        SchemaValidationService validator = new SchemaValidationService(new JsonValidator());
        // name in response is missing, schema expects required
        assertThrows(ValidationException.class, () -> validator.validateSchema(schema, response));
    }

    @Test
    void testThrowsWhenJsonInvalid() {
        String schema = """
                {
                       "type": "object",
                       "properties": {
                         "id": {
                           "type": "string"
                         },
                         "name": {
                           "type": "string"
                         },
                         "age": {
                           "type": "number"
                         },
                         "address": {
                           "type": "object",
                           "properties": {
                             "street": {
                               "type": "string"
                             },
                             "city": {
                               "type": "string"
                             },
                             "zipcode": {
                               "type": "string"
                             }
                           },
                           "required": ["street", "city", "zipcode"]
                         },
                         "tags": {
                           "type": "array"
                         }
                       },
                       "required": ["id", "name", "age", "address"]
                 }""";

        String response = """
                {
                  "id": 12345,
                  "name": "John Doe",
                  "age": 30,
                  "address": {
                    "street": "123 Main St",
                    "city": "Anytown",
                    "zipcode": "12345"
                  },
                  "tags": ["tag1", "tag2"]
                }""";

        SchemaValidationService validator = new SchemaValidationService(new JsonValidator());
        // id in response is number, schema expects string
        assertThrows(ValidationException.class, () -> validator.validateSchema(schema, response));
    }
}