package com.finflow.auth_users.services;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CodeGenerator {

    private static final String ALPHA_NUMERIC =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    private static final int CODE_LENGTH = 5;

    private final SecureRandom random = new SecureRandom();

    public String generateCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHA_NUMERIC.charAt(
                    random.nextInt(ALPHA_NUMERIC.length())
            ));
        }

        return sb.toString();
    }
}