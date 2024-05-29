package dev.jwtly10.dynatest.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.jwtly10.dynatest.context.TestContext;
import dev.jwtly10.dynatest.models.*;
import dev.jwtly10.dynatest.util.FunctionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TemplateParser {
    // Parse the request using the template parser and
    // - set any environment vars,
    // - set any variables from the context
    // - run any functions

    private final TestContext context;

    private final FunctionHandler functionHandler;

    public TemplateParser(TestContext context, FunctionHandler functionHandler) {
        this.context = context;
        this.functionHandler = functionHandler;
    }

    public Request parseRequest(Request request) throws Exception {
        try {
            request.setUrl(parseAndReplace(request.getUrl()));
            if (request.getQueryParams() != null) {
                request.setQueryParams(parseQueryParams(request.getQueryParams()));
            }
            if (request.getRequestHeaders() != null) {
                request.setRequestHeaders(parseHeaders(request.getRequestHeaders()));
            }
            if (request.getBody() != null) {
                try {
                    request.setJsonBody(parseJsonBody(request.getBody()));
                } catch (JsonProcessingException e) {
                    log.error("Error parsing json body", e);
                }
            }
            return request;
        } catch (Exception e) {
            log.error("Error parsing request due to: {}", e.getMessage());
            throw e;
        }

    }

    public void setStoredValues(Response response, StoreValues storeValues) {
        // TODO: Parse the reponse and set the context
    }

    private Headers parseHeaders(Headers headers) {
        headers.getHeaders().forEach((key, value) -> {
            try {
                headers.setHeader(key, parseAndReplace(value));
            } catch (Exception e) {
                log.error("Error parsing headers: {}", e.getMessage());
                // TODO: impl error context to handle this
            }
        });
        return headers;
    }

    private QueryParams parseQueryParams(QueryParams queryParams) {
        queryParams.getParams().forEach((key, value) -> {
            try {
                queryParams.setParam(key, parseAndReplace(value));
            } catch (Exception e) {
                log.error("Error parsing query params: {}", e.getMessage());
            }
        });
        return queryParams;
    }

    private JsonBody parseJsonBody(JsonBody jsonBody) throws Exception {
        String jsonBodyInString = JsonParser.toJson(jsonBody);
        log.debug("JsonBody was parsed into string: {}", jsonBodyInString);
        String replacedJson = parseAndReplace(jsonBodyInString);
        log.debug("JSON of JsonBody was templated into: {}", replacedJson);
        return JsonParser.fromJson(replacedJson, JsonBody.class);
    }

    private String parseAndReplace(String template) throws Exception {
        StringBuffer sb = new StringBuffer(template);
        int openBrace, closeBrace, nestedOpen, currentPos = 0;

        while ((openBrace = sb.indexOf("${", currentPos)) != -1) {
            closeBrace = sb.indexOf("}", openBrace);
            nestedOpen = sb.indexOf("${", openBrace + 2);

            while (nestedOpen != -1 && nestedOpen < closeBrace) {
                closeBrace = sb.indexOf("}", closeBrace + 1);
                nestedOpen = sb.indexOf("${", nestedOpen + 2);
            }

            String key = sb.substring(openBrace + 2, closeBrace);
            String replacement;

            if (functionHandler.isFunctionCall(key)) {
                String functionName = key.substring(0, key.indexOf('('));
                String rawArgs = key.substring(key.indexOf('(') + 1, key.lastIndexOf(')'));
                String[] args = parseArguments(rawArgs);

                // Recursively parse each argument
                for (int i = 0; i < args.length; i++) {
                    args[i] = parseAndReplace(args[i]);
                }

                log.info("Resolving function: {}", functionName);
                replacement = functionHandler.callFunction(functionName, args);
            } else {
                log.info("Resolving variable: {}", key);
                replacement = context.getValue(key);
            }

            sb.replace(openBrace, closeBrace + 1, replacement);
            currentPos = openBrace + replacement.length();
        }

        return sb.toString();
    }

    private String[] parseArguments(String rawArgs) {
        if (rawArgs.isEmpty()) {
            return new String[0];
        }

        String[] args = rawArgs.split(",");
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].trim();
        }
        return args;
    }

}