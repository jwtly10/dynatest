package dev.jwtly10.dynatest.handlers;

import dev.jwtly10.dynatest.exceptions.TestExecutionException;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ErrorContextHandler {
    private final List<TestExecutionException> errors = new ArrayList<>();

    public void addError(TestExecutionException ex) {
        errors.add(ex);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}