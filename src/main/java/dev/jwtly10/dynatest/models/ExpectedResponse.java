package dev.jwtly10.dynatest.models;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ExpectedResponse {
    private int statusCode;
    private ValidationSchema validationSchema;

    @Data
    public static class ValidationSchema {
        private String $schema = "http://json-schema.org/draft-07/schema#";
        private String type;
        private Map<String, Property> properties;
        private List<String> required;
    }

    @Data
    public static class Property {
        private String type;
        private Map<String, Property> properties;
        // TODO: For now we dont support validating arrays
        //        private List<Property> items;
        private List<String> required;
    }
}