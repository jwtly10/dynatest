package dev.jwtly10.dynatest.services;

import dev.jwtly10.dynatest.config.RestTemplateConfig;
import dev.jwtly10.dynatest.enums.Method;
import dev.jwtly10.dynatest.models.*;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpClientServiceTest {
    @Test
    void testValidClientGetRequest() {
        RestTemplate rt = new RestTemplateConfig().restTemplate();
        HttpClientService client = new HttpClientService(rt);

        Request request = Request.builder()
                .method(Method.GET)
                .url("https://httpbin.org/get")
                .requestHeaders(new Headers(Map.of("Content-Type", "application/json", "Accept", "application/json", "fakeapikey", "1234")))
                .build();

        Response res = client.makeRequest(request);

        assertEquals(200, res.getStatusCode());
        assertNotNull(res.getHeaders());
        assertNotNull(res.getJsonBody());
    }

    @Test
    void testQueryParamsParsing() {
        RestTemplate rt = new RestTemplateConfig().restTemplate();
        HttpClientService client = new HttpClientService(rt);

        Request request = Request.builder()
                .method(Method.GET)
                .url("https://httpbin.org/get")
                .queryParams(new QueryParams(Map.of("id", "1", "page", "2")))
                .build();

        Response res = client.makeRequest(request);

        assertEquals(200, res.getStatusCode());
        assertNotNull(res.getHeaders());

        var args = (Map<String, Object>) res.getJsonBody().get("args");
        assertEquals("1", args.get("id"));
        assertEquals("2", args.get("page"));
    }

    @Test
    void testValidClientPostRequest() {
        RestTemplate rt = new RestTemplateConfig().restTemplate();
        HttpClientService client = new HttpClientService(rt);

        JsonBody jsonBody = new JsonBody();
        jsonBody.setBodyData(Map.of(
                "name", "unit_test",
                "age", 10));

        Request request = Request.builder()
                .method(Method.POST)
                .url("https://httpbin.org/post")
                .jsonBody(jsonBody)
                .build();

        Response res = client.makeRequest(request);

        assertEquals(200, res.getStatusCode());
        assertNotNull(res.getHeaders());
        var json = (Map<String, Object>) res.getJsonBody().get("json");
        assertEquals("unit_test", json.get("name"));
    }

    @Test
    void testInvalidDomainClientRequest() {
        RestTemplate rt = new RestTemplateConfig().restTemplate();
        HttpClientService client = new HttpClientService(rt);

        Request request = Request.builder()
                .method(Method.GET)
                .url("https://dhttpbin.org/get2")
                .build();

        Response res = client.makeRequest(request);
        assertEquals(500, res.getStatusCode());
        assertNotNull(res.getJsonBody().get("error"));
    }

    @Test
    void testInvalidPathClientRequest() {
        RestTemplate rt = new RestTemplateConfig().restTemplate();
        HttpClientService client = new HttpClientService(rt);

        Request request = Request.builder()
                .method(Method.GET)
                .url("https://httpbin.org/get2")
                .build();

        Response res = client.makeRequest(request);
        assertEquals(404, res.getStatusCode());
    }
}