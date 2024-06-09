package dev.jwtly10.dynatest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.jwtly10.dynatest.dta.TestSuiteDataResponseBody;
import dev.jwtly10.dynatest.dta.TestSuiteRequestBody;
import dev.jwtly10.dynatest.models.TestSuite;
import dev.jwtly10.dynatest.models.TestSuiteEntity;
import dev.jwtly10.dynatest.parser.JsonParser;
import dev.jwtly10.dynatest.services.SuiteService;
import dev.jwtly10.dynatest.services.TestSuiteService;
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

    @PostMapping("/new")
    public TestSuiteEntity saveTestSuite(@RequestBody TestSuiteRequestBody req) {
        return testSuiteService.saveTestSuite(req.getName(), req.getConfiguration());
    }

    @DeleteMapping("/delete/{id}")
    public void deleteTestSuite(@PathVariable int id) {
        testSuiteService.deleteTestSuite(id);
    }

    @GetMapping
    public List<TestSuiteEntity> getAllTestSuites() {
        return testSuiteService.getAllActiveTestSuites();
    }

    @GetMapping("/{id}")
    public Optional<TestSuiteDataResponseBody> getTestSuiteById(@PathVariable int id) {
        // Doesnt just get test suite data, but also gets all meta and log data about the suite
        return suiteService.getAllDataFromTestSuite(id);
    }

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