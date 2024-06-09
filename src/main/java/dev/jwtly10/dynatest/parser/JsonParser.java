package dev.jwtly10.dynatest.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(json, clazz);
    }

    public static String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public static String formatError(JsonProcessingException e) {
        String message = e.getOriginalMessage();
        String location = e.getLocation() != null ? " at line: " + e.getLocation().getLineNr() + ", column: " + e.getLocation().getColumnNr() : "";
        return message + location;
    }
}