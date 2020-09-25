package com.example.innerstates;

import java.util.Random;

public class MyUtil {
    public static long getCurrentTime() {
        return System.currentTimeMillis() / 1000L;
    }

    public static int generateFiveDigit() {
        Random r = new Random( MyUtil.getCurrentTime() );
        return ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
    }
}
