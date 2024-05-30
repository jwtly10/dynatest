package dev.jwtly10.dynatest.config;

import dev.jwtly10.dynatest.context.TestContext;
import dev.jwtly10.dynatest.parser.TemplateParser;
import dev.jwtly10.dynatest.util.FunctionHandler;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemplateParserConfig {

    private final FunctionHandler functionHandler;

    public TemplateParserConfig(FunctionHandler functionHandler) {
        this.functionHandler = functionHandler;
    }

    public TemplateParser createTemplateParser(TestContext context) {
        return new TemplateParser(context, functionHandler);
    }
}