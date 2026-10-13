package com.ryderbelserion.common.utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

    public static boolean isChanceLess(final int min, final int max) {
        if (max <= min || max <= 0) return true;

        return 1+ThreadLocalRandom.current().nextInt(max) <= min;
    }

    public static int pickRandomNumber(final int max, final int min) {
        if (max == min) {
            return max;
        }

        return min + ThreadLocalRandom.current().nextInt(max - min);
    }
}