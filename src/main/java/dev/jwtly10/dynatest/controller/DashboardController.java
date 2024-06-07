package dev.jwtly10.dynatest.controller;

import dev.jwtly10.dynatest.models.TestSuiteEntity;
import dev.jwtly10.dynatest.services.TestSuiteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final TestSuiteService testSuiteService;

    public DashboardController(TestSuiteService testSuiteService) {
        this.testSuiteService = testSuiteService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        List<TestSuiteEntity> testSuites = testSuiteService.getAllActiveTestSuites();
        model.addAttribute("testSuites", testSuites);
        return "dashboard";
    }
}