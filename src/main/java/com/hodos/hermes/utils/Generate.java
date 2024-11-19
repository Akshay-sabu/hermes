package com.hodos.hermes.utils;

import java.util.Random;

public class Generate {
    private static final Random random = new Random();

    public static String GenerateOtp(){
        int randomNumber = random.nextInt(999999 - 100000 + 1) + 100000;
        return String.valueOf(randomNumber);
    }
}
