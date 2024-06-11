package dev.jwtly10.dynatest.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA512Hash {
    public static String getSHA512Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // Compute the hash
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));

            BigInteger no = new BigInteger(1, hashBytes);
            String hashText = no.toString(16);

            while (hashText.length() < 128) {
                hashText = "0" + hashText;
            }

            return hashText;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}