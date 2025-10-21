package com.badbones69.crazyenchantments.utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

    public static int getRandomNumber(final int min, final int max) {
        return ThreadLocalRandom.current().nextInt(max - min);
    }
}