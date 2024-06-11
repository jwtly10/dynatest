package dev.jwtly10.dynatest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.jwtly10.dynatest.dta.TestSuiteDataResponseBody;
import dev.jwtly10.dynatest.dta.TestSuiteRequestBody;
import dev.jwtly10.dynatest.models.TestSuite;
import dev.jwtly10.dynatest.models.TestSuiteEntity;
import dev.jwtly10.dynatest.parser.JsonParser;
import dev.jwtly10.dynatest.services.SuiteService;
import dev.jwtly10.dynatest.services.TestSuiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/test-suites")
@Slf4j
public class TestSuiteController {

    private final TestSuiteService testSuiteService;
    private final SuiteService suiteService;

    public TestSuiteController(TestSuiteService testSuiteService, SuiteService suiteService) {
        this.testSuiteService = testSuiteService;
        this.suiteService = suiteService;
    }

    @Operation(summary = "Save a new test suite")
    @PostMapping("/new")
    public TestSuiteEntity saveTestSuite(@RequestBody TestSuiteRequestBody req) {
        return testSuiteService.saveTestSuite(req.getName(), req.getConfiguration());
    }

    @Operation(summary = "Delete a test suite by ID")
    @DeleteMapping("/delete/{id}")
    public void deleteTestSuite(@PathVariable int id) {
        testSuiteService.deleteTestSuite(id);
    }

    @Operation(summary = "Get all active test suites")
    @GetMapping
    public List<TestSuiteEntity> getAllTestSuites() {
        return testSuiteService.getAllActiveTestSuites();
    }

    @Operation(summary = "Get a test suite by ID")
    @GetMapping("/{id}")
    public Optional<TestSuiteDataResponseBody> getTestSuiteById(@PathVariable int id) {
        return suiteService.getAllDataFromTestSuite(id);
    }

    @Operation(summary = "Update a test suite")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully",
                    content = @Content(schema = @Schema(implementation = TestSuite.class))),
            @ApiResponse(responseCode = "400", description = "Invalid JSON",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PutMapping("update/{id}")
    public ResponseEntity<?> updateTestSuite(@PathVariable int id, @RequestBody TestSuiteRequestBody req) {
        try {
            TestSuite suite = JsonParser.fromJson(req.getConfiguration(), TestSuite.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse config from request into TestSuite", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Test Suite JSON: " + JsonParser.formatError(e));
        }

        return ResponseEntity.ok(testSuiteService.updateTestSuite(id, req.getName(), req.getConfiguration()));
    }
}