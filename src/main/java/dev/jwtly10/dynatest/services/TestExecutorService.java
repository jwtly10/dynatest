package dev.jwtly10.dynatest.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.jwtly10.dynatest.enums.Status;
import dev.jwtly10.dynatest.enums.Type;
import dev.jwtly10.dynatest.exceptions.TestExecutionException;
import dev.jwtly10.dynatest.executor.TestExecutor;
import dev.jwtly10.dynatest.models.Log;
import dev.jwtly10.dynatest.models.TestSuite;
import dev.jwtly10.dynatest.models.TestSuiteEntity;
import dev.jwtly10.dynatest.models.TestSuiteRunLogEntity;
import dev.jwtly10.dynatest.parser.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TestExecutorService {
    private final TestSuiteService testSuiteService;

    private final TestExecutor executor;

    private final TestSuiteRunLogService testSuiteRunLogService;
    private final TestSuiteMetaService testSuiteMetaService;

    public TestExecutorService(TestSuiteService testSuiteService, TestExecutor executor, TestSuiteMetaService testSuiteMetaService, TestSuiteRunLogService testSuiteRunLogService) {
        this.testSuiteService = testSuiteService;
        this.executor = executor;
        this.testSuiteMetaService = testSuiteMetaService;
        this.testSuiteRunLogService = testSuiteRunLogService;
    }

    public void run(int id) {
        // 1. A request is made with an ID of a test suite to run it

        // 2. We get the json configuration from DB
        TestSuiteEntity entity = testSuiteService.getTestSuiteById(id).orElseThrow();

        // 3. We parse it
        TestSuite testSuite;
        try {
            testSuite = JsonParser.fromJson(entity.getConfiguration(), TestSuite.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON from db with id {}", id, e);
            return;
        }
        // 4. We initialise any logs we need to (test_suite_run_log_tb)
        TestSuiteRunLogEntity runLog = testSuiteRunLogService.startRunForTestSuite(id);

        // 5. We run the test using the TestExecutor.class
        List<Log> runLogs = new ArrayList<>();
        runLogs.add(Log.of(Type.INFO, "Starting test execution for test suite '%s'", entity.getName()));
        try {
            executor.runTestSuite(testSuite, runLogs);
            runLogs.add(Log.of(Type.PASS, "Test execution PASSED for test suite '%s'", entity.getName()));
            runLogs.add(Log.of(Type.OUTPUT_REPORT, handleOutputReport(testSuite)));
            testSuiteRunLogService.finishRun(runLog.getId());
            handleOutcome(entity, Status.SUCCESS, runLogs);
        } catch (TestExecutionException e) {
            runLogs.add(Log.of(Type.FAIL, "Test execution FAILED for test suite '%s'", entity.getName()));
            testSuiteRunLogService.failRun(runLog.getId());
            handleOutcome(entity, Status.FAIL, runLogs);
        }

    }

    private String handleOutputReport(TestSuite suite) {
        StringBuilder outputReport = new StringBuilder();
        outputReport.append(String.format("Ran %s tests!\n", suite.getTests().size()));
        suite.getTests().forEach((test) -> {
            outputReport.append(String.format("Test '%s' had %s step(s)\n", test.getName(), test.getSteps().size()));
        });

        return outputReport.toString();
    }

    private void handleOutcome(TestSuiteEntity entity, Status outcome, List<Log> logs) {
        // Convert runLogs to string
        String stringLogs = "";
        try {
            stringLogs = JsonParser.toJson(logs);
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON Logs", e);
        }
        testSuiteMetaService.saveMetaDataForTestSuiteRun(entity.getId(), outcome, stringLogs);

    }
}