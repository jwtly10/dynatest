package dev.jwtly10.dynatest.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.jwtly10.dynatest.config.TemplateParserConfig;
import dev.jwtly10.dynatest.context.TestContext;
import dev.jwtly10.dynatest.enums.Type;
import dev.jwtly10.dynatest.exceptions.SchemaValidationException;
import dev.jwtly10.dynatest.exceptions.TemplateParserException;
import dev.jwtly10.dynatest.exceptions.TestExecutionException;
import dev.jwtly10.dynatest.models.*;
import dev.jwtly10.dynatest.parser.JsonParser;
import dev.jwtly10.dynatest.parser.TemplateParser;
import dev.jwtly10.dynatest.services.HttpClientService;
import dev.jwtly10.dynatest.services.SchemaValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
@Slf4j
public class TestExecutor {

    private final TemplateParserConfig templateParserConfig;
    private final HttpClientService client;
    private final SchemaValidationService validator;


    public TestExecutor(TemplateParserConfig templateParserConfig, HttpClientService client, SchemaValidationService validator) {
        this.templateParserConfig = templateParserConfig;
        this.client = client;
        this.validator = validator;
    }

    public void runTestSuite(TestSuite testSuite, List<Log> runLogs) throws TestExecutionException {
        for (TestRun test : testSuite.getTests()) {
            executeTest(test, runLogs);
        }
    }

    private void executeTest(TestRun test, List<Log> runLogs) throws TestExecutionException {
        log.info("Executing test '{}'", test.getName());
        runLogs.add(Log.of(Type.INFO, "Executing test '%s'", test.getName()));
        TestContext context = new TestContext();
        TemplateParser templateParser = templateParserConfig.createTemplateParser(context);
        // Set local vars and environment vars
        try {
            templateParser.setVars(test, runLogs);
        } catch (TemplateParserException e) {
            log.error("Error parsing local vars", e);
            throw new TestExecutionException(e.getMessage(), e);
        }

        for (Step step : test.getSteps()) {
            try {
                log.info("Running step '{}'", step.getStepName());
                runLogs.add(Log.of(Type.INFO, "Running step '%s'", step.getStepName()));
                Response res = doStep(step, templateParser, runLogs);
                log.debug("Response from request: {}", res);

                // Here is where we do validation
                if (step.getExpectedResponse() != null) {
                    log.debug("Expected response : {}", step.getExpectedResponse());
                    validator.validateResponse(res, step.getExpectedResponse());
                    runLogs.add(Log.of(Type.DEBUG, "Response passed validation schema"));
                } else {
                    runLogs.add(Log.of(Type.WARN, "No validation schema found"));
                }
                runLogs.add(Log.of(Type.STEP_PASS, "STEP PASSED", step.getStepName()));
            } catch (TestExecutionException e) {
                log.error("Error executing step '{}'", step.getStepName(), e);
                throw new TestExecutionException("Step '" + step.getStepName() + "' failed with error: ", e);
            } catch (SchemaValidationException e) {
                log.error("Response schema validation failed for step '{}'", step.getStepName(), e);
                runLogs.add(Log.of(Type.ERROR, "Step '" + step.getStepName() + "' failed with validation error: " + e.getMessage()));
                throw new TestExecutionException("Step '" + step.getStepName() + "' failed with error: " + e.getMessage(), e);
            }
        }
    }

    private Response doStep(Step step, TemplateParser templateParser, List<Log> runLogs) throws TestExecutionException {
        Request req = null;
        try {
            req = templateParser.parseRequest(step.getRequest(), runLogs);
        } catch (TemplateParserException e) {
            runLogs.add(Log.of(Type.ERROR, e.getMessage()));
            throw new TestExecutionException("Error parsing request", e);
        }
        try {
            return doRequest(req, templateParser, runLogs);
        } catch (TestExecutionException e) {
            runLogs.add(Log.of(Type.ERROR, e.getMessage()));
            throw new TestExecutionException("Error executing request", e);
        }
    }

    private Response doRequest(Request req, TemplateParser templateParser, List<Log> runLogs) throws TestExecutionException {
        log.debug("Executing request '{}'", req);
        runLogs.add(Log.of(Type.DEBUG, "Executing request"));
        try {
            Response res = client.makeRequest(req);
            runLogs.add(Log.of(Type.INFO, "Got response with status: %s", res.getStatusCode()));
            String resBodyString = "";
            try {
                resBodyString = JsonParser.toJson(res.getJsonBody());
                runLogs.add(Log.of(Type.JSON, res.getRawBody()));
            } catch (JsonProcessingException e) {
                log.error("Error parsing response body");
                runLogs.add(Log.of(Type.ERROR, "Failed to process response body"));
            }

            // Set context from stored values
            templateParser.setStoredValues(res, req.getStoreValues(), runLogs);

            return res;
        } catch (RestClientException | TemplateParserException e) {
            throw new TestExecutionException(e);
        }
    }
}