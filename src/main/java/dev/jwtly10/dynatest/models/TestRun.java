package dev.jwtly10.dynatest.models;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@Jacksonized
public class TestRun {
    private String name;
    private String description;
    private List<Step> steps;
}