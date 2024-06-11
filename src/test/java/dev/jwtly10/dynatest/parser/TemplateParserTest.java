package dev.jwtly10.dynatest.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.jwtly10.dynatest.context.TestContext;
import dev.jwtly10.dynatest.exceptions.TemplateParserException;
import dev.jwtly10.dynatest.models.*;
import dev.jwtly10.dynatest.util.FunctionHandler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TemplateParserTest {

    @Test
    public void testCanStoreInitialLocalVarInTest() throws JsonProcessingException, TemplateParserException {
        String json = """
                {
                      "name": "Test Name",
                      "vars": {
                        "url": "https://www.httpbin.com/get"
                      },
                      "description": "Description of test",
                      "steps": [
                        {
                          "stepName": "Step",
                          "request": {
                            "method": "GET",
                            "url": "${url}",
                            "headers": {
                              "Authorization": "Bearer token"
                            },
                            "body": {
                              "name": "test",
                              "age": 10,
                              "address": {
                                  "city": "London",
                                  "country": "UK"
                              }
                            }
                          }
                        }
                      ]
                    }
                """;

        TestRun test = JsonParser.fromJson(json, TestRun.class);

        TestContext context = new TestContext();
        FunctionHandler handler = new FunctionHandler();
        TemplateParser templateParser = new TemplateParser(context, handler);

        templateParser.setVars(test, new ArrayList<>());
        assertEquals("https://www.httpbin.com/get", context.getValue("url"));

        Request parsedReq = templateParser.parseRequest(test.getSteps().get(0).getRequest(), new ArrayList<>());

        assertEquals("https://www.httpbin.com/get", parsedReq.getUrl());
    }

    @Test
    public void testCanStoreResponseVariable() throws RuntimeException, TemplateParserException {
        JsonBody body = new JsonBody();
        body.setBodyData(Map.of(
                "user_id", "3",
                "age", "25",
                "address.city", "london"
        ));
        Response res = Response.builder()
                .headers(new Headers(Map.of()))
                .jsonBody(body)
                .build();

        StoreValues vals = new StoreValues();
        vals.setValues(Map.of(
                "id", "body.user_id",
                "city", "body.address.city"
        ));

        TestContext context = new TestContext();
        FunctionHandler handler = new FunctionHandler();
        TemplateParser templateParser = new TemplateParser(context, handler);

        templateParser.setStoredValues(res, vals, new ArrayList<>());

        assertEquals("3", context.getValue("id"));
        assertEquals("london", context.getValue("city"));
    }

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

        List<Log> logs = new ArrayList<>();
        Request parsedRequest = templateParser.parseRequest(req, logs);

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

        List<Log> logs = new ArrayList<>();
        Request parsedRequest = templateParser.parseRequest(req, logs);

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

        List<Log> logs = new ArrayList<>();
        Request parsedRequest = templateParser.parseRequest(req, logs);

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

        List<Log> logs = new ArrayList<>();
        Request parsedRequest = templateParser.parseRequest(req, logs);
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

        List<Log> logs = new ArrayList<>();
        assertThrows(TemplateParserException.class, () -> templateParser.parseRequest(req, logs));
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

        List<Log> logs = new ArrayList<>();
        assertThrows(TemplateParserException.class, () -> templateParser.parseRequest(req, logs));
    }

    @Test
    public void testThrowsWhenTemplateCreatesInvalidJson() throws JsonProcessingException {
        String json = """
                {
                    "method": "POST",
                    "url": "https://api.one/create",
                    "headers": {
                      "Authorization": "Bearer ${token}"
                    },
                    "body": {
                      "emptyEmail": "${invalidString}"
                    }
                }
                """;

        Request req = JsonParser.fromJson(json, Request.class);

        TestContext context = new TestContext();
        context.setVariable("invalidString", "\"invalid}}}},}");
        FunctionHandler handler = new FunctionHandler();
        TemplateParser templateParser = new TemplateParser(context, handler);

        context.setVariable("token", "test_token");

        List<Log> logs = new ArrayList<>();
        assertThrows(TemplateParserException.class, () -> templateParser.parseRequest(req, logs));
    }

    @Test
    public void testThrowsWhenLocalVarsCallInvalidFunction() throws JsonProcessingException, TemplateParserException {
        String json = """
                {
                      "name": "Test Name",
                      "vars": {
                        "url": "${INVALID}"
                      },
                      "description": "Description of test",
                      "steps": [
                        {
                          "stepName": "Step",
                          "request": {
                            "method": "GET",
                            "url": "${url}",
                            "headers": {
                              "Authorization": "Bearer token"
                            },
                            "body": {
                              "name": "test",
                              "age": 10,
                              "address": {
                                  "city": "London",
                                  "country": "UK"
                              }
                            }
                          }
                        }
                      ]
                    }
                """;

        TestRun test = JsonParser.fromJson(json, TestRun.class);

        TestContext context = new TestContext();
        FunctionHandler handler = new FunctionHandler();
        TemplateParser templateParser = new TemplateParser(context, handler);

        assertThrows(TemplateParserException.class, () -> templateParser.setVars(test, new ArrayList<>()));
    }
}