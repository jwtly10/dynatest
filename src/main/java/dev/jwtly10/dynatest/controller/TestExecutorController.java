package dev.jwtly10.dynatest.controller;

import dev.jwtly10.dynatest.dta.TestSuiteDataResponseBody;
import dev.jwtly10.dynatest.services.SuiteService;
import dev.jwtly10.dynatest.services.TestExecutorService;
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

    @GetMapping("/run/{id}")
    public Optional<TestSuiteDataResponseBody> run(@PathVariable int id) {
        executorService.run(id);
        return suiteService.getAllDataFromTestSuite(id);
    }
}