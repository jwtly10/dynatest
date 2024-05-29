package dev.jwtly10.dynatest.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.jwtly10.dynatest.models.Headers;
import dev.jwtly10.dynatest.models.JsonBody;
import dev.jwtly10.dynatest.models.Request;
import dev.jwtly10.dynatest.models.Response;
import dev.jwtly10.dynatest.util.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;


@Service
@Slf4j
public class HttpClientService implements HttpService {

    private final RestTemplate restTemplate;

    public HttpClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Response makeRequest(Request request) throws RestClientException {
        log.info("Making new client request");
        log.debug("Request: {}", request);

        HttpHeaders headers = new HttpHeaders();
        request.getHeaders().forEach(headers::set);

        Map<String, String> queryParams = request.getParams();
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(request.getUrl());
        queryParams.forEach(urlBuilder::queryParam);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request.getJsonBody(), headers);

        ResponseEntity<String> res = null;
        JsonBody responseJsonBodyMap = new JsonBody();
        try {
            res = restTemplate.exchange(urlBuilder.toUriString(), request.getMethod(), entity, String.class);
        } catch (Exception e) {
            log.error("Error making http request", e);
            responseJsonBodyMap.setBodyData(Map.of(
                    "error", "Error making request",
                    "raw", e.getMessage() != null ? e.getMessage() : "No stacktrace")
            );
        }

        log.debug("Response: {}", res);

        Headers responseHeaders = new Headers();
        if (res != null) {
            res.getHeaders();
            res.getHeaders().forEach((k, v) -> responseHeaders.setHeader(k, v.get(0)));

            try {
                responseJsonBodyMap = JsonParser.fromJson(res.getBody(), JsonBody.class);
            } catch (JsonProcessingException e) {
                log.error("Error parsing response body", e);
                responseJsonBodyMap.setBodyData(Map.of(
                        "error", "Error parsing response body",
                        "type", responseHeaders.getHeader("Content-Type") != null ? responseHeaders.getHeader("Content-Type") : "No Content-Type",
                        "raw", res.getBody() != null ? res.getBody() : "No response body")
                );
            }
        }

        return Response.builder()
                .jsonBody(responseJsonBodyMap)
                .statusCode(res == null ? 500 : res.getStatusCode().value())
                .headers(responseHeaders)
                .build();
    }
}