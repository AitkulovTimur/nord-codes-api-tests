package com.nordcodes.api.utils;

import java.security.SecureRandom;


/**
 * Utility class for generating random tokens (by the test-task condition).
 * <p>
 * This class provides a method to generate secure random tokens consisting of
 * uppercase letters and digits, suitable for use in authentication and testing scenarios.
 * </p>
 */
public final class TokenGenerator {

    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TOKEN_LENGTH = 32;
    private static final SecureRandom RANDOM = new SecureRandom();

    private TokenGenerator() {
    }

    /**
     * Generates a random alphanumeric token of fixed length.
     * <p>
     * The token consists of 32 characters randomly selected from uppercase letters (A-Z)
     * and digits (0-9). Uses a cryptographically secure random number generator.
     * </p>
     *
     * @return a randomly generated 32-character token
     */
    public static String validToken() {
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);

        for (int i = 0; i < TOKEN_LENGTH; i++) {
            int randomIndex = RANDOM.nextInt(ALLOWED_CHARS.length());
            token.append(ALLOWED_CHARS.charAt(randomIndex));
        }

        return token.toString();
    }
}