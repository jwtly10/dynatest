package dev.jwtly10.dynatest.functions;

import dev.jwtly10.dynatest.util.SHA512Hash;

public class TemplateFunctions {
    public static String randomEmail() {
        return "test" + System.currentTimeMillis() + "@mailinator.com";
    }

    public static String randomEmail(String prefix) {
        return prefix + System.currentTimeMillis() + "@mailinator.com";
    }

    public static String hash(String key, String timestamp) {
        String hash = SHA512Hash.getSHA512Hash(key + timestamp);
        return hash.toUpperCase();
    }

    public static String now() {
        // Get current epoch in seconds
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    public static String concat(String a, String b) {
        return a + b;
    }
}