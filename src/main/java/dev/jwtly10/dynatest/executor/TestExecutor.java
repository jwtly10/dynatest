package dev.jwtly10.dynatest.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.jwtly10.dynatest.config.TemplateParserConfig;
import dev.jwtly10.dynatest.context.TestContext;
import dev.jwtly10.dynatest.enums.Type;
import dev.jwtly10.dynatest.exceptions.TemplateParserException;
import dev.jwtly10.dynatest.exceptions.TestExecutionException;
import dev.jwtly10.dynatest.models.*;
import dev.jwtly10.dynatest.parser.JsonParser;
import dev.jwtly10.dynatest.parser.TemplateParser;
import dev.jwtly10.dynatest.services.HttpClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
@Slf4j
public class TestExecutor {

    private final TemplateParserConfig templateParserConfig;
    private final HttpClientService client;

    public TestExecutor(TemplateParserConfig templateParserConfig, HttpClientService client) {
        this.templateParserConfig = templateParserConfig;
        this.client = client;
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

        for (Step step : test.getSteps()) {
            try {
                log.info("Running step '{}'", step.getStepName());
                runLogs.add(Log.of(Type.INFO, "Running step '%s'", step.getStepName()));
                Response res = doStep(step, templateParser, runLogs);
                log.debug("Response from request: {}", res);
                // Here is where we do validation on the stuff we expected in the step
            } catch (TestExecutionException e) {
                log.error("Error executing step '{}'", step.getStepName(), e);
                throw new TestExecutionException("Step '" + step.getStepName() + "' failed with error: ", e);
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
        runLogs.add(Log.of(Type.INFO, "Executing request"));
        try {
            Response res = client.makeRequest(req);
            runLogs.add(Log.of(Type.INFO, "Got response with status: %s", res.getStatusCode()));
            String resBodyString = "";
            try {
                resBodyString = JsonParser.toJson(res.getJsonBody());
                runLogs.add(Log.of(Type.JSON, resBodyString));
            } catch (JsonProcessingException e) {
                log.error("Error parsing response body");
                runLogs.add(Log.of(Type.ERROR, "Failed to process response body"));
            }

            // Set context from stored values
            templateParser.setStoredValues(res, req.getStoreValues());

            return res;
        } catch (RestClientException | TemplateParserException e) {
            throw new TestExecutionException(e);
        }
    }
}