package dev.jwtly10.dynatest.executor;

import dev.jwtly10.dynatest.config.TemplateParserConfig;
import dev.jwtly10.dynatest.context.TestContext;
import dev.jwtly10.dynatest.models.*;
import dev.jwtly10.dynatest.parser.TemplateParser;
import dev.jwtly10.dynatest.services.HttpClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
                log.info(res.toString());
                // Here is where we do validation on the stuff we expected in the step
            } catch (Exception e) {
                log.error("Error executing step '{}'", step.getStepName(), e);
            }
        }
    }

    private Response doStep(Step step, TemplateParser templateParser) throws Exception {
        Request req = templateParser.parseRequest(step.getRequest());
        return doRequest(req, templateParser);
    }

    private Response doRequest(Request req, TemplateParser templateParser) {
        log.debug("Executing request '{}'", req);
        Response res = client.makeRequest(req);
        // Set context from stored values
        templateParser.setStoredValues(res, req.getStoreValues());

        return res;
    }
}