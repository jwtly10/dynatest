package dev.jwtly10.dynatest.util;

public class TemplateFunctions {
    public static String randomEmail() {
        return "test" + System.currentTimeMillis() + "@mailinator.com";
    }

    public static String randomEmail(String prefix) {
        return prefix + System.currentTimeMillis() + "@mailinator.com";
    }

    public static String hash(String timestamp, String key) {
        return "hash";
    }

    public static String now() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static String concat(String a, String b) {
        return a + b;
    }
}