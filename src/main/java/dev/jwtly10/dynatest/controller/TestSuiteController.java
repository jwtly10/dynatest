package dev.jwtly10.dynatest.controller;

import dev.jwtly10.dynatest.dta.TestSuiteRequestBody;
import dev.jwtly10.dynatest.models.TestSuiteEntity;
import dev.jwtly10.dynatest.services.TestSuiteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/test-suites")
public class TestSuiteController {

    private final TestSuiteService testSuiteService;

    public TestSuiteController(TestSuiteService testSuiteService) {
        this.testSuiteService = testSuiteService;
    }

    @PostMapping("/new")
    public TestSuiteEntity saveTestSuite(@RequestBody TestSuiteRequestBody req) {
        return testSuiteService.saveTestSuite(req.getName(), req.getConfiguration());
    }

    @GetMapping
    public List<TestSuiteEntity> getAllTestSuites() {
        return testSuiteService.getAllTestSuites();
    }

    @GetMapping("/{id}")
    public Optional<TestSuiteEntity> getTestSuiteById(@PathVariable int id) {
        return testSuiteService.getTestSuiteById(id);
    }

    @PutMapping("/{id}")
    public TestSuiteEntity updateTestSuite(@PathVariable int id, @RequestBody TestSuiteRequestBody req) {
        return testSuiteService.updateTestSuite(id, req.getName(), req.getConfiguration());
    }
}