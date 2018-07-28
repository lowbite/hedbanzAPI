package com.hedbanz.hedbanzAPI.utils;

import java.util.Random;

public class KeyWordGenerator {
    private final static String CHARS = "ABCDEFGHJKLMNOPQRSTUVWXYZ234567890";


    public static String getGeneratedKeyWord(int length) {
        Random random = new Random();
        StringBuilder token = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            token.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return token.toString();
    }
}
