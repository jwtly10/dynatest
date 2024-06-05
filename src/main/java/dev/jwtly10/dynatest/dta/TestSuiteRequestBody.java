package dev.jwtly10.dynatest.dta;

import lombok.Data;

@Data
public class TestSuiteRequestBody {
    private String name;
    private String configuration;
}