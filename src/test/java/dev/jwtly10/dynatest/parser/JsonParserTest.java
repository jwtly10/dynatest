package dev.jwtly10.dynatest.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.jwtly10.dynatest.models.Request;
import dev.jwtly10.dynatest.models.TestRun;
import dev.jwtly10.dynatest.models.TestSuite;
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
            assertEquals("test", request.getJsonBody().get("name"));
            assertEquals(10, request.getJsonBody().get("age"));
            assertEquals("London", request.getJsonBody().get("address.city"));
            assertEquals("UK", request.getJsonBody().get("address.country"));
        });
    }

    @Test
    void testCanParseTestSuiteModelFromJson() throws JsonProcessingException {
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
                            },
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

        TestSuite actualTestSuite = JsonParser.fromJson(json, TestSuite.class);

        assertEquals(2, actualTestSuite.getTests().size());
        assertEquals("Test Scenario Name 1", actualTestSuite.getTests().get(0).getName());
        assertEquals("Description of what this test scenario does", actualTestSuite.getTests().get(0).getDescription());
        assertEquals(1, actualTestSuite.getTests().get(0).getSteps().size());
        assertEquals("Unique Step Name", actualTestSuite.getTests().get(0).getSteps().get(0).getStepName());
        assertEquals(HttpMethod.GET, actualTestSuite.getTests().get(0).getSteps().get(0).getRequest().getMethod());
        assertEquals("https://api.example.com/resource", actualTestSuite.getTests().get(0).getSteps().get(0).getRequest().getUrl());
        assertEquals("application/json", actualTestSuite.getTests().get(0).getSteps().get(0).getRequest().getHeader("Content-Type"));
        assertEquals("{previousValue}", actualTestSuite.getTests().get(0).getSteps().get(0).getRequest().getParam("id"));
        assertEquals(10, actualTestSuite.getTests().get(0).getSteps().get(0).getRequest().getJsonBody().get("age"));

        assertEquals("Test Scenario Name 2", actualTestSuite.getTests().get(1).getName());
        assertEquals("Description of what this test scenario does", actualTestSuite.getTests().get(1).getDescription());
        assertEquals("Unique Step Name", actualTestSuite.getTests().get(1).getSteps().get(0).getStepName());
        assertEquals(HttpMethod.GET, actualTestSuite.getTests().get(1).getSteps().get(0).getRequest().getMethod());
        assertEquals("https://api.example.com/resource", actualTestSuite.getTests().get(1).getSteps().get(0).getRequest().getUrl());
        assertEquals("Bearer {token}", actualTestSuite.getTests().get(1).getSteps().get(0).getRequest().getHeader("Authorization"));
        assertEquals("{previousValue}", actualTestSuite.getTests().get(1).getSteps().get(0).getRequest().getParam("id"));
        assertEquals(new HashMap<>(), actualTestSuite.getTests().get(1).getSteps().get(0).getRequest().getJsonBody());
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

        assertNull(actualTestRun.getSteps().get(0).getRequest().getRequestHeaders());
    }

    @Test
    void testCanHandleArraysInBody() throws JsonProcessingException {
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
                            "queryParams": {
                              "id": "{previousValue}"
                            },
                            "body": {
                                "address":{
                                    "city": "London",
                                    "country": "UK"
                                },
                              "names": [
                                {"name1": "Josh"},
                                {
                                    "name2": "NewName",
                                    "address":{
                                        "city": "London",
                                        "country": "UK"
                                    }
                                }
                              ]
                            }
                          }
                        }
                      ]
                    }
                """;

        TestRun actualTestRun = JsonParser.fromJson(json, TestRun.class);
        System.out.println(actualTestRun.getSteps().get(0).getRequest().getBody());
        System.out.println(actualTestRun.getSteps().get(0).getRequest().getJsonBody());
        System.out.println(actualTestRun.getSteps().get(0).getRequest().getJsonBody().get("address.city"));
        System.out.println(actualTestRun.getSteps().get(0).getRequest().getJsonBody().get("names"));
        // TODO: We dont handle json arrays very well. This is something I will need to implement
    }
}