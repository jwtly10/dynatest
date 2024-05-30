package dev.jwtly10.dynatest.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Slf4j
public class FunctionHandler {
    public boolean isFunctionCall(String expression) {
        return expression.contains("(") && expression.contains(")");
    }

    public String callFunction(String functionName, String[] args) throws Exception {
        log.debug("Invoking method: {} with args: {} (argCount: {})", functionName, args, args.length);
        Method method = findMethod(functionName, args.length);
        return (String) method.invoke(this, (Object[]) args);
    }

    public Method findMethod(String name, int argCount) throws NoSuchMethodException {
        for (Method method : TemplateFunctions.class.getMethods()) {
            if (method.getName().equals(name) && method.getParameterCount() == argCount) {
                log.debug("Found method: {}", method);
                return method;
            }
        }
        throw new NoSuchMethodException("Method " + name + " with " + argCount + " arguments not found.");
    }
}