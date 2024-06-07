package dev.jwtly10.dynatest.exceptions;

public class TemplateParserException extends Exception {
    public TemplateParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateParserException(String message) {
        super(message);
    }
}