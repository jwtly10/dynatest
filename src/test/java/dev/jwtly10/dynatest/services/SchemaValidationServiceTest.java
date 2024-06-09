package dev.jwtly10.dynatest.services;

import dev.jwtly10.dynatest.util.JsonValidator;
import org.everit.json.schema.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class SchemaValidationServiceTest {

    private final SchemaValidationService validator = new SchemaValidationService(new JsonValidator());

    @Test
    void testCanValidateRealResponse() {
        String schema = """
                {
                  "type": "object",
                  "properties": {
                    "args": {
                      "type": "object",
                      "properties": {},
                      "required": []
                    },
                    "headers": {
                      "type": "object",
                      "properties": {
                        "Accept": {
                          "type": "string"
                        },
                        "Content-Type": {
                          "type": "string"
                        },
                        "Host": {
                          "type": "string"
                        },
                        "User-Agent": {
                          "type": "string"
                        },
                        "X-Amzn-Trace-Id": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "Accept",
                        "Content-Type",
                        "Host",
                        "User-Agent",
                        "X-Amzn-Trace-Id"
                      ]
                    },
                    "origin": {
                      "type": "string"
                    },
                    "url": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "args",
                    "headers",
                    "origin",
                    "url"
                  ]
                }""";

        String response = """
                {
                  "args": {},
                  "headers": {
                    "Accept": "application/json",
                    "Content-Type": "application/json",
                    "Host": "httpbin.org",
                    "User-Agent": "Java/17.0.9",
                    "X-Amzn-Trace-Id": "Root=1-6666144f-6a53e71a780e215029e3978b"
                  },
                  "origin": "82.14.33.176",
                  "url": "https://httpbin.org/get"
                }
                """;

        try {
            validator.validateSchema(schema, response);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void testCanValidateSchema() throws Exception {
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

        try {
            validator.validateSchema(schema, response);
        } catch (Exception e) {
            fail(e);
        }
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

        // id in response is number, schema expects string
        assertThrows(ValidationException.class, () -> validator.validateSchema(schema, response));
    }
}