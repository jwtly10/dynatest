package dev.jwtly10.dynatest.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
@Slf4j
public class NoOpResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        // Return false to never treat responses as errors
        if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
            log.error("Error response: {} {}", response.getStatusCode(), response.getStatusText());
        }
        return false;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        // Do nothing
    }
}