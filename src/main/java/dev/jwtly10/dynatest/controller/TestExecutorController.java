package dev.jwtly10.dynatest.controller;

import dev.jwtly10.dynatest.dta.TestSuiteDataResponseBody;
import dev.jwtly10.dynatest.services.SuiteService;
import dev.jwtly10.dynatest.services.TestExecutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/execute")
public class TestExecutorController {

    private final TestExecutorService executorService;
    private final SuiteService suiteService;

    public TestExecutorController(TestExecutorService executorService, SuiteService suiteService) {
        this.executorService = executorService;
        this.suiteService = suiteService;
    }

    @Operation(summary = "Run a test suite by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test suite executed successfully"),
            @ApiResponse(responseCode = "404", description = "Test suite not found")
    })
    @GetMapping("/run/{id}")
    public Optional<TestSuiteDataResponseBody> run(@PathVariable int id) {
        executorService.run(id);
        return suiteService.getAllDataFromTestSuite(id);
    }
}