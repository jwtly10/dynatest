package dev.jwtly10.dynatest.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.jwtly10.dynatest.models.Request;
import dev.jwtly10.dynatest.models.Response;
import dev.jwtly10.dynatest.models.TestList;
import dev.jwtly10.dynatest.models.TestRun;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JsonParserTest {
    @Test
    void testCanParseTestRunModelFromJson() throws JsonProcessingException {
        String json = """
                    {
                      "name": "Test Scenario Name",
                      "description": "Description of what this test scenario does",
                      "steps": [
                        {
                          "stepName": "Unique Step Name",
                          "request": {
                            "method": "GET",
                            "url": "https://api.example.com/resource",
                            "headers": {
                              "Authorization": "Bearer {token}"
                            },
                            "queryParams": {
                              "id": "{previousValue}"
                            },
                            "body": {
                              "name": "test",
                              "age": 10,
                              "address": {
                                  "city": "London",
                                  "country": "UK"
                              }
                            }
                          },
                          "response": {
                            "statusCode": 200,
                            "headers":{
                              "Content-Type": "application/json"
                            },
                            "body":{
                                "message":"successful request"
                            },
                            "storeValues": {
                              "token": "responseBody.token",
                              "userId": "responseBody.user.id"
                            }
                          }
                        }
                      ]
                    }
                """;

        TestRun actualTestRun = JsonParser.fromJson(json, TestRun.class);

        assertEquals("Test Scenario Name", actualTestRun.getName());
        assertEquals("Description of what this test scenario does", actualTestRun.getDescription());
        assertEquals(1, actualTestRun.getSteps().size());
        actualTestRun.getSteps().forEach(step -> {
            assertEquals("Unique Step Name", step.getStepName());

            Request request = step.getRequest();
            assertEquals(HttpMethod.GET, request.getMethod());
            assertEquals("https://api.example.com/resource", request.getUrl());
            assertEquals("Bearer {token}", request.getHeader("Authorization"));
            assertEquals("{previousValue}", request.getParam("id"));
            assertEquals("test", request.getBody().get("name"));
            assertEquals(10, request.getBody().get("age"));
            assertEquals("London", request.getBody().get("address.city"));
            assertEquals("UK", request.getBody().get("address.country"));

            Response response = step.getResponse();
            assertEquals(200, response.getStatusCode());
            assertEquals("application/json", response.getHeader("Content-Type"));
            assertEquals("successful request", response.getBody().get("message"));
            assertEquals("responseBody.token", response.getStoredValue("token"));
            assertEquals("responseBody.user.id", response.getStoredValue("userId"));
        });
    }

    @Test
    void testCanParseTestListModelFromJson() throws JsonProcessingException {
        String json = """
                {
                  "tests": [
                    {
                      "name": "Test Scenario Name 1",
                      "description": "Description of what this test scenario does",
                      "steps": [
                        {
                          "stepName": "Unique Step Name",
                          "request": {
                            "method": "GET",
                            "url": "https://api.example.com/resource",
                            "headers": {
                              "Content-Type": "application/json"
                            },
                            "queryParams": {
                              "id": "{previousValue}"
                            },
                            "body": {
                              "age": 10
                            }
                          },
                          "response": {
                            "statusCode": 200,
                            "headers": {
                              "Authorization": "Bearer {token}"
                            },
                            "storeValues": {
                              "token": "responseBody.token",
                              "userId": "responseBody.user.id"
                            }
                          }
                        }
                      ]
                    },
                    {
                      "name": "Test Scenario Name 2",
                      "description": "Description of what this test scenario does",
                      "steps": [
                        {
                          "stepName": "Unique Step Name",
                          "request": {
                            "method": "GET",
                            "url": "https://api.example.com/resource",
                            "headers": {
                              "Authorization": "Bearer {token}"
                            },
                            "queryParams": {
                              "id": "{previousValue}"
                            }
                          },
                          "response": {
                            "statusCode": 200,
                            "storeValues": {
                              "token": "responseBody.token",
                              "userId": "responseBody.user.id"
                            }
                          }
                        }
                      ]
                    }
                  ]
                }
                """;

        TestList actualTestList = JsonParser.fromJson(json, TestList.class);

        assertEquals(2, actualTestList.getTests().size());
        assertEquals("Test Scenario Name 1", actualTestList.getTests().get(0).getName());
        assertEquals("Description of what this test scenario does", actualTestList.getTests().get(0).getDescription());
        assertEquals(1, actualTestList.getTests().get(0).getSteps().size());
        assertEquals("Unique Step Name", actualTestList.getTests().get(0).getSteps().get(0).getStepName());
        assertEquals(HttpMethod.GET, actualTestList.getTests().get(0).getSteps().get(0).getRequest().getMethod());
        assertEquals("https://api.example.com/resource", actualTestList.getTests().get(0).getSteps().get(0).getRequest().getUrl());
        assertEquals("application/json", actualTestList.getTests().get(0).getSteps().get(0).getRequest().getHeader("Content-Type"));
        assertEquals("{previousValue}", actualTestList.getTests().get(0).getSteps().get(0).getRequest().getParam("id"));
        assertEquals(10, actualTestList.getTests().get(0).getSteps().get(0).getRequest().getBody().get("age"));
        assertEquals(200, actualTestList.getTests().get(0).getSteps().get(0).getResponse().getStatusCode());
        assertEquals("Bearer {token}", actualTestList.getTests().get(0).getSteps().get(0).getResponse().getHeader("Authorization"));
        assertEquals("responseBody.token", actualTestList.getTests().get(0).getSteps().get(0).getResponse().getStoredValues().get("token"));
        assertEquals("responseBody.user.id", actualTestList.getTests().get(0).getSteps().get(0).getResponse().getStoredValues().get("userId"));

        assertEquals("Test Scenario Name 2", actualTestList.getTests().get(1).getName());
        assertEquals("Description of what this test scenario does", actualTestList.getTests().get(1).getDescription());
        assertEquals("Unique Step Name", actualTestList.getTests().get(1).getSteps().get(0).getStepName());
        assertEquals(HttpMethod.GET, actualTestList.getTests().get(1).getSteps().get(0).getRequest().getMethod());
        assertEquals("https://api.example.com/resource", actualTestList.getTests().get(1).getSteps().get(0).getRequest().getUrl());
        assertEquals("Bearer {token}", actualTestList.getTests().get(1).getSteps().get(0).getRequest().getHeader("Authorization"));
        assertEquals("{previousValue}", actualTestList.getTests().get(1).getSteps().get(0).getRequest().getParam("id"));
        assertEquals(new HashMap<>(), actualTestList.getTests().get(1).getSteps().get(0).getRequest().getBody());
        assertEquals(200, actualTestList.getTests().get(1).getSteps().get(0).getResponse().getStatusCode());
        assertEquals(new HashMap<>(), actualTestList.getTests().get(1).getSteps().get(0).getResponse().getHeaders());
        assertEquals("responseBody.token", actualTestList.getTests().get(1).getSteps().get(0).getResponse().getStoredValues().get("token"));
        assertEquals("responseBody.user.id", actualTestList.getTests().get(1).getSteps().get(0).getResponse().getStoredValues().get("userId"));
    }

    @Test
    void testCanHandleMissingValues() throws JsonProcessingException {
        String json = """
                    {
                      "name": "Test Scenario Name",
                      "description": "Description of what this test scenario does",
                      "steps": [
                        {
                          "stepName": "Unique Step Name",
                          "request": {
                            "method": "GET",
                            "url": "https://api.example.com/resource",
                            "headers": {
                              "Authorization": "Bearer {token}"
                            },
                            "queryParams": {
                              "id": "{previousValue}"
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

        TestRun actualTestRun = JsonParser.fromJson(json, TestRun.class);

        assertNull(actualTestRun.getSteps().get(0).getResponse());
    }
}