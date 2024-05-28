package dev.jwtly10.dynatest.models;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@Jacksonized
public class TestList {
    private List<TestRun> tests;
}