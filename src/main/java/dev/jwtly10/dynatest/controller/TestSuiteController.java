package dev.jwtly10.dynatest.controller;

import dev.jwtly10.dynatest.dta.TestSuiteDataResponseBody;
import dev.jwtly10.dynatest.dta.TestSuiteRequestBody;
import dev.jwtly10.dynatest.models.TestSuiteEntity;
import dev.jwtly10.dynatest.services.SuiteService;
import dev.jwtly10.dynatest.services.TestSuiteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/test-suites")
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
    public TestSuiteEntity updateTestSuite(@PathVariable int id, @RequestBody TestSuiteRequestBody req) {
        return testSuiteService.updateTestSuite(id, req.getName(), req.getConfiguration());
    }
}