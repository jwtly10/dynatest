package dev.jwtly10.dynatest.executor;

import dev.jwtly10.dynatest.config.TemplateParserConfig;
import dev.jwtly10.dynatest.context.TestContext;
import dev.jwtly10.dynatest.exceptions.TemplateParserException;
import dev.jwtly10.dynatest.exceptions.TestExecutionException;
import dev.jwtly10.dynatest.models.*;
import dev.jwtly10.dynatest.parser.TemplateParser;
import dev.jwtly10.dynatest.services.HttpClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
@Slf4j
public class TestExecutor {

    private final TemplateParserConfig templateParserConfig;
    private final HttpClientService client;

    public TestExecutor(TemplateParserConfig templateParserConfig, HttpClientService client) {
        this.templateParserConfig = templateParserConfig;
        this.client = client;
    }

    public void runTestSuit(TestList testList) {
        for (TestRun test : testList.getTests()) {
            executeTest(test);
        }
    }

    private void executeTest(TestRun test) {
        log.info("Executing test '{}'", test.getName());
        TestContext context = new TestContext();
        TemplateParser templateParser = templateParserConfig.createTemplateParser(context);

        for (Step step : test.getSteps()) {
            try {
                log.info("Running step '{}'", step.getStepName());
                Response res = doStep(step, templateParser);
                log.debug("Response from request: {}", res);
                // Here is where we do validation on the stuff we expected in the step
            } catch (TestExecutionException e) {
                log.error("Error executing step '{}'", step.getStepName(), e);
                break;
            }
        }
    }

    private Response doStep(Step step, TemplateParser templateParser) throws TestExecutionException {
        try {
            Request req = templateParser.parseRequest(step.getRequest());
            return doRequest(req, templateParser);
        } catch (TemplateParserException e) {
            throw new TestExecutionException(e);
        }
    }

    private Response doRequest(Request req, TemplateParser templateParser) throws TestExecutionException {
        log.debug("Executing request '{}'", req);
        try {
            Response res = client.makeRequest(req);

            // Set context from stored values
            templateParser.setStoredValues(res, req.getStoreValues());

            return res;
        } catch (RestClientException | TemplateParserException e) {
            throw new TestExecutionException(e);
        }
    }
}