package dev.jwtly10.dynatest.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ExpectedResponse {
    private int statusCode;
    private ValidationSchema validationSchema;

    @Data
    public static class ValidationSchema {
        private String $schema = "http://json-schema.org/draft-04/schema#";
        private String type;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Map<String, Property> properties;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<String> required;
    }

    @Data
    public static class Property {
        private String type;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Map<String, Property> properties;
        // TODO: For now we dont support validating arrays
        //        private List<Property> items;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<String> required;
    }
}