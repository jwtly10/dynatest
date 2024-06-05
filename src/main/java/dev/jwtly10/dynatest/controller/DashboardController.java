package dev.jwtly10.dynatest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {

    @GetMapping("/")
    public String dashboard(Model model) {
        // Creating dummy data
        List<TestSuite> suites = new ArrayList<>();

        // Suite 1
        List<TestStep> steps1 = List.of(
                new TestStep("Step 1", "Passed"),
                new TestStep("Step 2", "Failed")
        );

        List<Test> tests1 = List.of(
                new Test("Test 1", steps1),
                new Test("Test 2", steps1)
        );

        TestSuite suite1 = new TestSuite(1, "Suite 1", tests1);
        suites.add(suite1);

        // Suite 2
        List<TestStep> steps2 = List.of(
                new TestStep("Step 1", "Passed"),
                new TestStep("Step 2", "Passed")
        );

        List<Test> tests2 = List.of(
                new Test("Test 1", steps2),
                new Test("Test 2", steps2)
        );

        TestSuite suite2 = new TestSuite(2, "Suite 2", tests2);
        suites.add(suite2);

        // Adding data to the model
        model.addAttribute("suites", suites);
        model.addAttribute("selectedSuite", suite1); // Set a default selected suite

        return "dashboard";
    }

    // Dummy classes for test data
    static class TestSuite {
        private int id;
        private String name;
        private List<Test> tests;

        public TestSuite(int id, String name, List<Test> tests) {
            this.id = id;
            this.name = name;
            this.tests = tests;
        }

        // Getters and setters
        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public List<Test> getTests() {
            return tests;
        }
    }

    static class Test {
        private String name;
        private List<TestStep> steps;

        public Test(String name, List<TestStep> steps) {
            this.name = name;
            this.steps = steps;
        }

        // Getters and setters
        public String getName() {
            return name;
        }

        public List<TestStep> getSteps() {
            return steps;
        }
    }

    static class TestStep {
        private String name;
        private String status;

        public TestStep(String name, String status) {
            this.name = name;
            this.status = status;
        }

        // Getters and setters
        public String getName() {
            return name;
        }

        public String getStatus() {
            return status;
        }
    }
}