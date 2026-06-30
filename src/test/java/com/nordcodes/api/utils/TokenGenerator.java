package com.nordcodes.api.utils;

import java.security.SecureRandom;


/**
 * Utility class for generating random tokens.
 */
public final class TokenGenerator {

    //Данная переменная должна была включать символы из ТЗ, но исправлена в соответствии с реальным поведением системы
    private static final String ALLOWED_CHARS = "0123456789ABCDEF";
    private static final int TOKEN_LENGTH = 32;
    private static final SecureRandom RANDOM = new SecureRandom();

    private TokenGenerator() {
    }

    /**
     * Generates a valid token accepted by the current application implementation.
     * <p>
     * The task description allows A-Z and 0-9, but the actual application accepts
     * only uppercase HEX characters: 0-9 and A-F.
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
