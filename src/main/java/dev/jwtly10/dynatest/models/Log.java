package dev.jwtly10.dynatest.models;

import dev.jwtly10.dynatest.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Log {
    private Type type;
    private String message;

    public static Log of(Type type, String message, Object... args) {
        return new Log(type, String.format(message, args));
    }
}