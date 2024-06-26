package dev.jwtly10.dynatest.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.jwtly10.dynatest.context.TestContext;
import dev.jwtly10.dynatest.enums.Type;
import dev.jwtly10.dynatest.exceptions.TemplateParserException;
import dev.jwtly10.dynatest.handlers.FunctionHandler;
import dev.jwtly10.dynatest.models.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
public class TemplateParser {
    private final TestContext context;
    private final FunctionHandler functionHandler;

    public TemplateParser(TestContext context, FunctionHandler functionHandler) {
        this.context = context;
        this.functionHandler = functionHandler;
    }

    public Request parseRequest(Request request, List<Log> runLogs) throws TemplateParserException {
        request.setUrl(parseUrl(request.getUrl(), runLogs));
        if (request.getQueryParams() != null) {
            request.setQueryParams(parseQueryParams(request.getQueryParams(), runLogs));
        }
        if (request.getRequestHeaders() != null) {
            request.setRequestHeaders(parseHeaders(request.getRequestHeaders(), runLogs));
        }
        if (request.getBody() != null) {
            request.setJsonBody(parseJsonBody(request.getBody(), runLogs));
        }
        return request;
    }

    public void setStoredValues(Response response, StoreValues storeValues, List<Log> runLogs) throws TemplateParserException {
        // For now we only support the response body, but this is built in a way it can be extended
        // Just need to add a parser for the field

        // Note also, arrays are not supported as target storeValue objects

        // If no stored values just leave
        if (storeValues == null) {
            return;
        }

        Map<String, String> vals = storeValues.getValues();

        for (Map.Entry<String, String> entry : vals.entrySet()) {
            log.info("Resolving stored value '{}'", entry.getValue());
            runLogs.add(Log.of(Type.DEBUG, "Resolving stored value '%s'", entry.getValue()));
            String[] params = entry.getValue().split("\\.", 2);
            for (int i = 0; i < params.length; i++) {
                params[i] = params[i].trim();
            }

            switch (params[0]) {
                case ("body"):
                    Map<String, Object> body = response.getJsonBody();

                    // TODO: Better way to do this? It should be 'safe' but wont be best UX
                    String val = body.get(params[1]).toString();
                    log.debug("Found '{}'", val);
                    runLogs.add(Log.of(Type.DEBUG, "Found '%s'", val));

                    if (val != null) {
                        context.setVariable(entry.getKey(), val);
                    } else {
                        throw new RuntimeException("Cannot find field '" + params[1] + "' in '" + params[0] + "'");
                    }

                    break;
                default:
                    throw new TemplateParserException("Invalid response access");
            }
        }
    }

    private String parseUrl(String url, List<Log> runLogs) throws TemplateParserException {
        return parseAndReplace(url, runLogs);
    }

    private Headers parseHeaders(Headers headers, List<Log> runLogs) throws TemplateParserException {
        Map<String, String> originalHeaders = headers.getHeaders();
        for (Map.Entry<String, String> entry : originalHeaders.entrySet()) {
            headers.setHeader(entry.getKey(), parseAndReplace(entry.getValue(), runLogs));
        }
        return headers;
    }

    private QueryParams parseQueryParams(QueryParams queryParams, List<Log> runLogs) throws TemplateParserException {
        Map<String, String> originalParams = queryParams.getParams();
        for (Map.Entry<String, String> entry : originalParams.entrySet()) {
            queryParams.setParam(entry.getKey(), parseAndReplace(entry.getValue(), runLogs));
        }
        return queryParams;
    }

    private Map<String, String> parseVars(Map<String, String> vars, List<Log> runLogs) throws TemplateParserException {
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            vars.put(entry.getKey(), parseAndReplace(entry.getValue(), runLogs));
        }
        return vars;
    }

    private JsonBody parseJsonBody(JsonBody jsonBody, List<Log> runLogs) throws TemplateParserException {
        String jsonBodyInString = "";
        try {
            jsonBodyInString = JsonParser.toJson(jsonBody);
        } catch (JsonProcessingException e) {
            throw new TemplateParserException("Unable to parse request JSON: " + e.getMessage());
        }
        log.debug("JsonBody was parsed into string: {}", jsonBodyInString);
        String replacedJson = parseAndReplace(jsonBodyInString, runLogs);
        log.debug("JSON of JsonBody was templated into: {}", replacedJson);
        try {
            return JsonParser.fromJson(replacedJson, JsonBody.class);
        } catch (JsonProcessingException e) {
            throw new TemplateParserException("Unable to parse request JSON after templating: " + e.getMessage());
        }
    }

    private String parseAndReplace(String template, List<Log> runLogs) throws TemplateParserException {
        log.info("Parsing template: {}", template);
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
            // TODO: Check this is fine, basically if we fail to parse we just reset to the template str
            String replacement = "${" + key + "}";

            if (functionHandler.isFunctionCall(key)) {
                String functionName = key.substring(0, key.indexOf('('));
                String rawArgs = key.substring(key.indexOf('(') + 1, key.lastIndexOf(')'));
                String[] args = parseArguments(rawArgs);

                // Recursively parse each argument
                for (int i = 0; i < args.length; i++) {
                    args[i] = parseAndReplace(args[i], runLogs);
                }

                log.info("Resolving function: '{}'", functionName);
                runLogs.add(Log.of(Type.DEBUG, "Resolving template function '%s'", functionName));
                try {
                    replacement = functionHandler.callFunction(functionName, args);
                } catch (Exception e) {
                    throw new TemplateParserException("Unable to invoke function '" + functionName + "': " + e.getMessage(), e);
                }
            } else {
                log.info("Resolving variable: '{}'", key);
                runLogs.add(Log.of(Type.DEBUG, "Resolving variable '%s'", key));
                try {
                    replacement = context.getValue(key);
                } catch (NoSuchElementException e) {
                    runLogs.add(Log.of(Type.ERROR, "Unable to resolve variable '%s'", key));
                    throw new TemplateParserException("'" + key + "' doesn't exist in the test runner variable context. Did you define this as a 'storedValue' in a previous request?");
                }
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

    public void setVars(TestRun test, List<Log> runLogs) throws TemplateParserException {
        // Sets local environment vars
        if (test.getVars() == null) {
            log.info("No local vars to store");
            runLogs.add(Log.of(Type.DEBUG, "No local vars to parse"));
            return;
        }

        Map<String, String> parsedVars = parseVars(test.getVars(), runLogs);
        for (String key : parsedVars.keySet()) {
            log.info("Found local var {} with value {}", key, parsedVars.get(key));
            runLogs.add(Log.of(Type.DEBUG, "Found local var %s with values %s", key, parsedVars.get(key)));
            context.setVariable(key, parsedVars.get(key));
        }
    }
}