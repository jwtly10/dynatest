package dev.jwtly10.dynatest.models;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class Step {
    private String stepName;
    private Request request;
    private Response response;
}