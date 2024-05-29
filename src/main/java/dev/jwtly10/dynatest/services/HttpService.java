package dev.jwtly10.dynatest.services;

import dev.jwtly10.dynatest.models.Request;
import dev.jwtly10.dynatest.models.Response;

public interface HttpService {
    Response makeRequest(Request request) throws Exception;
}