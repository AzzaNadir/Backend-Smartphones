package com.example.model;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenGenerator {
    private static final int TOKEN_LENGTH = 64;

    public static String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}
