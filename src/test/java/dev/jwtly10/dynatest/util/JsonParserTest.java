package dev.jwtly10.dynatest.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.jwtly10.dynatest.enums.Method;
import dev.jwtly10.dynatest.models.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class JsonParserTest {
    // TODO: Refactor the expectedTestRun and expectedTestList generations
    @Test
    void TestCanParseTestModel() {
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
                              "name": "test"
                            }
                          },
                          "response": {
                            "statusCode": 200,
                            "headers":{
                              "Content-Type": "application/json"
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

        try {
            TestRun actualTestRun = JsonParser.fromJson(json, TestRun.class);

            TestRun expectedTestRun = TestRun.builder()
                    .name("Test Scenario Name")
                    .description("Description of what this test scenario does")
                    .steps(Collections.singletonList(Step.builder().stepName("Unique Step Name")
                            .request(Request.builder()
                                    .method(Method.GET)
                                    .url("https://api.example.com/resource")
                                    .headers(new Headers(new HashMap<>() {{
                                        put("Authorization", "Bearer {token}");
                                    }}))
                                    .queryParams(new QueryParams(new HashMap<>() {{
                                        put("id", "{previousValue}");
                                    }}))
                                    .body(new Body(new HashMap<>() {{
                                        put("name", "test");
                                    }}))
                                    .build())
                            .response(Response.builder()
                                    .statusCode(200)
                                    .headers(new Headers(new HashMap<>() {{
                                        put("Content-Type", "application/json");
                                    }}))
                                    .storeValues(new StoreValues(new HashMap<>() {{
                                        put("token", "responseBody.token");
                                        put("userId", "responseBody.user.id");
                                    }}))
                                    .build())
                            .build()))
                    .build();

            assertEquals(expectedTestRun, actualTestRun);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("Failed to parse TestRun Json");
        }
    }

    @Test
    void TestCanParseTestsModel() {
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
                            },
                            "body": {
                              "key": "value"
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

        try {
            TestList actualTestList = JsonParser.fromJson(json, TestList.class);

            TestRun expectedTestRun1 = TestRun.builder()
                    .name("Test Scenario Name 1")
                    .description("Description of what this test scenario does")
                    .steps(Collections.singletonList(Step.builder().stepName("Unique Step Name")
                            .request(Request.builder()
                                    .method(Method.GET)
                                    .url("https://api.example.com/resource")
                                    .headers(new Headers(new HashMap<>() {{
                                        put("Content-Type", "application/json");
                                    }}))
                                    .queryParams(new QueryParams(new HashMap<>() {{
                                        put("id", "{previousValue}");
                                    }}))
                                    .body(new Body(new HashMap<>() {{
                                        put("age", "10");
                                    }}))
                                    .build())
                            .response(Response.builder()
                                    .statusCode(200)
                                    .headers(new Headers(new HashMap<>() {{
                                        put("Authorization", "Bearer {token}");
                                    }}))
                                    .storeValues(new StoreValues(new HashMap<>() {{
                                        put("token", "responseBody.token");
                                        put("userId", "responseBody.user.id");
                                    }}))
                                    .build())
                            .build()))
                    .build();

            TestRun expectedTestRun2 = TestRun.builder()
                    .name("Test Scenario Name 2")
                    .description("Description of what this test scenario does")
                    .steps(Collections.singletonList(Step.builder().stepName("Unique Step Name")
                            .request(Request.builder()
                                    .method(Method.GET)
                                    .url("https://api.example.com/resource")
                                    .headers(new Headers(new HashMap<>() {{
                                        put("Authorization", "Bearer {token}");
                                    }}))
                                    .queryParams(new QueryParams(new HashMap<>() {{
                                        put("id", "{previousValue}");
                                    }}))
                                    .body(new Body(new HashMap<>() {{
                                        put("key", "value");
                                    }}))
                                    .build())
                            .response(Response.builder()
                                    .statusCode(200)
                                    .storeValues(new StoreValues(new HashMap<>() {{
                                        put("token", "responseBody.token");
                                        put("userId", "responseBody.user.id");
                                    }}))
                                    .build())
                            .build()))
                    .build();

            TestList expectedTestList = TestList.builder()
                    .tests(Arrays.asList(expectedTestRun1, expectedTestRun2))
                    .build();

            assertEquals(expectedTestList, actualTestList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("Failed to parse TestList");
        }


    }
}