package dev.jwtly10.dynatest.executor;

import dev.jwtly10.dynatest.config.TemplateParserConfig;
import dev.jwtly10.dynatest.models.*;
import dev.jwtly10.dynatest.parser.JsonParser;
import dev.jwtly10.dynatest.services.HttpClientService;
import dev.jwtly10.dynatest.util.FunctionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TestExecutorTest {

    @Mock
    private HttpClientService mockClient;

    @Captor
    private ArgumentCaptor<Request> requestCaptor;

    @Test
    void runTestSuite() throws Exception {
        String json = """
                {
                  "tests": [
                    {
                      "name": "Creates new user, and querys for a book",
                      "description": "This test creates a new user, and then queries for a book",
                      "steps": [
                        {
                          "stepName": "Get Id for new user",
                          "request": {
                            "method": "POST",
                            "url": "https://api.one/create",
                            "headers": {
                              "Authorization": "Bearer token"
                            },
                            "body": {
                              "email": "test@gmail.com"
                            },
                            "storeValues": {
                              "userId": "body.generatedId"
                            }
                          }
                        },
                        {
                          "stepName": "Queries for a book with the user id",
                          "request": {
                            "method": "GET",
                            "url": "https://api.one/getBooks",
                            "headers": {
                              "Authorization": "Bearer token"
                            },
                            "queryParams": {
                              "userId": "${userId}"
                            }
                          }
                        }
                      ]
                    }
                  ]
                }
                """;

        TestSuite testSuite = JsonParser.fromJson(json, TestSuite.class);

        JsonBody mockBody = new JsonBody();
        mockBody.setBodyData(Map.of(
                "generatedId", "123456789"
        ));
        Response mockResponse = Response.builder()
                .jsonBody(mockBody)
                .build();

        when(mockClient.makeRequest(Mockito.any())).thenReturn(mockResponse);

        FunctionHandler handler = new FunctionHandler();
        TemplateParserConfig config = new TemplateParserConfig(handler);
        TestExecutor executor = new TestExecutor(config, mockClient);

        List<Log> logs = new ArrayList<>();
        executor.runTestSuite(testSuite, logs);

        verify(mockClient, times(2)).makeRequest(requestCaptor.capture());
        List<Request> capturedRequests = requestCaptor.getAllValues();

        // Why?
        // The second time the request is made, we expect that the parameters from storeValue in the
        // first request are injected into the second requests.
        Request secondRequest = capturedRequests.get(1);
        assertEquals("123456789", secondRequest.getQueryParams().getParam("userId"));
    }
}