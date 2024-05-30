package dev.jwtly10.dynatest.exceptions;

public class TestExecutionException extends Exception {
    public TestExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestExecutionException(Throwable cause) {
        super(cause);
    }
}