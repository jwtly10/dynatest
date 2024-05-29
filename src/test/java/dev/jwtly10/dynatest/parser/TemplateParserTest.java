package dev.jwtly10.dynatest.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.jwtly10.dynatest.context.TestContext;
import dev.jwtly10.dynatest.models.Request;
import dev.jwtly10.dynatest.util.FunctionHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TemplateParserTest {
    @Test
    public void testCanParseVariablesInRequest() throws Exception {
        String json = """
                {
                    "method": "POST",
                    "url": "https://api.one/create",
                    "headers": {
                      "Authorization": "Bearer ${token}"
                    },
                    "body": {
                      "email": "${email}"
                    }
                }
                """;

        Request req = JsonParser.fromJson(json, Request.class);

        TestContext context = new TestContext();
        FunctionHandler handler = new FunctionHandler();
        TemplateParser templateParser = new TemplateParser(context, handler);

        context.setVariable("token", "test_token");
        context.setVariable("email", "test_email@email.com");

        Request parsedRequest = templateParser.parseRequest(req);

        assertEquals("Bearer test_token", parsedRequest.getHeader("Authorization"));
        assertEquals("test_email@email.com", parsedRequest.getJsonBody().get("email"));
    }

    @Test
    public void testCanParseSimpleFunctionsInRequest() throws Exception {
        String json = """
                {
                    "method": "POST",
                    "url": "https://api.one/create",
                    "headers": {
                      "Authorization": "Bearer ${token}"
                    },
                    "body": {
                      "email": "${randomEmail()}"
                    }
                }
                """;

        Request req = JsonParser.fromJson(json, Request.class);

        TestContext context = new TestContext();
        FunctionHandler handler = new FunctionHandler();
        TemplateParser templateParser = new TemplateParser(context, handler);

        context.setVariable("token", "test_token");
        context.setEnvVariable("apiKey", "test_api_key");

        Request parsedRequest = templateParser.parseRequest(req);

        assertEquals("Bearer test_token", parsedRequest.getHeader("Authorization"));
        assertEquals("test", parsedRequest.getJsonBody().get("email").toString().substring(0, 4));
    }

    @Test
    public void testCanParseFunctionsWithParamsInRequest() throws Exception {
        String json = """
                {
                    "method": "POST",
                    "url": "https://api.one/create",
                    "headers": {
                      "Authorization": "Bearer ${token}"
                    },
                    "body": {
                      "emptyEmail": "${randomEmail()}",
                      "prefixedEmail": "${randomEmail(${prefix})}"
                    }
                }
                """;

        Request req = JsonParser.fromJson(json, Request.class);

        TestContext context = new TestContext();
        FunctionHandler handler = new FunctionHandler();
        TemplateParser templateParser = new TemplateParser(context, handler);

        context.setVariable("token", "test_token");
        context.setEnvVariable("apiKey", "test_api_key");
        context.setVariable("prefix", "josh");

        Request parsedRequest = templateParser.parseRequest(req);

        assertEquals("Bearer test_token", parsedRequest.getHeader("Authorization"));
        assertEquals("josh", parsedRequest.getJsonBody().get("prefixedEmail").toString().substring(0, 4));

        System.out.println(parsedRequest);
    }

    @Test
    public void testCanParseNestedFunctionsInRequest() throws Exception {
        String json = """
                {
                    "method": "POST",
                    "url": "https://api.one/create",
                    "body": {
                      "concatString": "${concat(${now()}, ${name})}"
                    }
                }
                """;

        Request req = JsonParser.fromJson(json, Request.class);

        TestContext context = new TestContext();
        FunctionHandler handler = new FunctionHandler();
        TemplateParser templateParser = new TemplateParser(context, handler);

        context.setVariable("name", "josh");

        Request parsedRequest = templateParser.parseRequest(req);
        System.out.println(parsedRequest);

        int len = parsedRequest.getJsonBody().get("concatString").toString().length();
        assertTrue(len > 4);
        assertEquals("josh", parsedRequest.getJsonBody().get("concatString").toString().substring(len - 4));
        assertTrue(!parsedRequest.getJsonBody().get("concatString").toString().contains(" "));


    }

    @Test
    public void testThrowsWhenMissingContext() throws Exception {
        String json = """
                {
                    "method": "POST",
                    "url": "https://api.one/${userId}"
                }
                """;

        Request req = JsonParser.fromJson(json, Request.class);

        TestContext context = new TestContext();
        FunctionHandler handler = new FunctionHandler();
        TemplateParser templateParser = new TemplateParser(context, handler);

        // Missing value
        // context.setVariable("userId", "1");

        assertThrows(RuntimeException.class, () -> templateParser.parseRequest(req));
    }

    @Test
    public void testThrowsWhenMissingFunction() throws JsonProcessingException {
        String json = """
                {
                    "method": "POST",
                    "url": "https://api.one/create",
                    "headers": {
                      "Authorization": "Bearer ${token}"
                    },
                    "body": {
                      "emptyEmail": "${randomEmailMissing()}"
                    }
                }
                """;

        Request req = JsonParser.fromJson(json, Request.class);

        TestContext context = new TestContext();
        FunctionHandler handler = new FunctionHandler();
        TemplateParser templateParser = new TemplateParser(context, handler);

        context.setVariable("token", "test_token");

        assertThrows(NoSuchMethodException.class, () -> templateParser.parseRequest(req));
    }
}