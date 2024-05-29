package dev.jwtly10.dynatest.executor;

import dev.jwtly10.dynatest.context.TestContext;
import dev.jwtly10.dynatest.models.TestRun;
import dev.jwtly10.dynatest.parser.TemplateParser;
import dev.jwtly10.dynatest.util.FunctionHandler;
import org.springframework.stereotype.Service;

@Service
public class TestExecutor {

    private TestContext context;
    private FunctionHandler functionHandler;
    private TemplateParser templateParser;

    public void execute(TestRun test) {
        // 1. Parse the test request using the template parser and
        // - set any environment vars,
        // - set any variables from the context
        // - run any functions

        // 2. Execute the test request

        // 3. Check stored values and update the context
        // 4. (Later) Validate the response against the expectedResponse


    }
}