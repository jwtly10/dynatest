package dev.jwtly10.dynatest.controller;

import dev.jwtly10.dynatest.services.TestExecutorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/execute")
public class TestExecutorController {

    private final TestExecutorService executorService;

    public TestExecutorController(TestExecutorService executorService) {
        this.executorService = executorService;
    }

    @GetMapping("/run/{id}")
    public String run(@PathVariable int id) {
        executorService.run(id);
        return "Success";
    }
}