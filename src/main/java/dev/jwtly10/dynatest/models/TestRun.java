package dev.jwtly10.dynatest.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Map;

@Data
@Builder
@Jacksonized
public class TestRun {
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> vars;

    private String description;
    private List<Step> steps;
}