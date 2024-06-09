package dev.jwtly10.dynatest.exceptions;

public class SchemaValidationException extends Exception {
    public SchemaValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SchemaValidationException(String message) {
        super(message);
    }
}